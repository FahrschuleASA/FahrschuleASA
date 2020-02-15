import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    LOCALE_ID,
    ViewEncapsulation,
    OnInit,
    Inject
} from '@angular/core';
import {
    CalendarDateFormatter,
    CalendarEvent,
    CalendarEventTitleFormatter,
    CalendarView,
    DAYS_OF_WEEK,
    CalendarEventTimesChangedEvent
} from 'angular-calendar';
import {LocaleDateFormatter} from "app/home/calendar-utils/locale.date.formatter";
import {CustomEventTitleFormatter} from "app/home/calendar-utils/eventtitle.formatter";
import {WeekViewHourSegment} from 'calendar-utils';
import {fromEvent, Observable, Subject, BehaviorSubject} from 'rxjs';
import {finalize, takeUntil} from 'rxjs/operators';
import {addDays, addMinutes, endOfWeek, parseISO} from 'date-fns'
import {TheoryLesson} from 'app/shared/model/theory-lesson.model'
import {CalendarService} from 'app/home/calendar-utils/calendar.service';
import {TimeSlot} from "app/shared/model/time-slot.model";
import {Preference} from "app/shared/model/preference.model";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {TheoryLessonModalComponent} from "app/home/modals/theory-lesson/theory-lesson.modal.component";
import {JhiEventManager} from 'ng-jhipster';
import {DrivingLesson} from "app/shared/model/driving-lesson.model";
import {EventType} from "app/shared/model/enumerations/event-type.model";

function floorToNearestCell(amount: number, precision: number) {
    return Math.floor(amount / precision) * precision;
}

function ceilToNearestCell(amount: number, precision: number) {
    return Math.ceil(amount / precision) * precision;
}

@Component({
    selector: 'jhi-home',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './home.component.html',
    styleUrls: ['home.scss'],
    providers: [
        {
            provide: CalendarDateFormatter,
            useClass: LocaleDateFormatter
        },
        {
            provide: CalendarEventTitleFormatter,
            useClass: CustomEventTitleFormatter
        }
    ],
    styles: [
            `
            .disable-text-selection {
                -webkit-touch-callout: none;
                -webkit-user-select: none;
                -khtml-user-select: none;
                -moz-user-select: none;
                -ms-user-select: none;
                user-select: none;
            }
        `
    ],
    encapsulation: ViewEncapsulation.None
})
export class HomeComponent implements OnInit {
    view: CalendarView = CalendarView.Month;

    viewDate: Date = new Date();

    events: CalendarEvent[] = [];
    eventsSubject: BehaviorSubject<CalendarEvent[]>;

    locale: string = this.localeId;

    weekStartsOn: DAYS_OF_WEEK.MONDAY = DAYS_OF_WEEK.MONDAY;
    weekendDays: number[] = [DAYS_OF_WEEK.SUNDAY];

    dragToCreateActive = false;

    refresh: Subject<any> = new Subject<any>();

    sidebarOpen: Subject<boolean> = new Subject<boolean>();
    sidebarEvent: Subject<CalendarEvent> = new Subject<CalendarEvent>();

    count: 0 = 0;

    constructor(
        private cdr: ChangeDetectorRef,
        private calendarService: CalendarService,
        @Inject(LOCALE_ID) private localeId: string,
        private modalService: NgbModal,
        private eventManager: JhiEventManager
    ) {
    }

    claimId(): number {
        this.count++;
        return this.count;
    }

    registerCloseSidebarListener(): void {
        this.eventManager.subscribe('closeSidebar', () => {
            this.sidebarOpen.next(false);
        });
    }

    registerEventChangeContentListener(): void {
        this.eventManager.subscribe('refreshCalendarView', () => {
            this.refreshView();
        });
    }

    registerDeleteEventListener(): void {
        this.eventManager.subscribe('deleteCalendarEvent', ({name, content}) => {
            this.removeEvent(content);
        });
        this.refreshView();
    }

    registerDeleteAllTheoryLessonsListener(): void {
        this.eventManager.subscribe('deleteAllTheoryLessons', () => {
            this.events = this.events.filter(event => event.meta.type !== EventType.THEORY_LESSON);
            this.refreshView();
            this.displayTheoryLessons(this.calendarService.fetchTheoryLessons());
        });
    }

    registerDeleteAllDrivingLessonsListener(): void {
        this.eventManager.subscribe('deleteAllDrivingLessons', () => {
            this.events = this.events.filter(event => event.meta.type !== EventType.DRIVING_LESSON);
            this.refreshView();
            this.displayDrivingLessons(this.calendarService.fetchDrivingLessons());
        });
    }

    registerDeleteAllUnassignedDrivingLessonsListener(): void {
        this.eventManager.subscribe('deleteAllUnassignedDrivingLessons', () => {
            this.events = this.events.filter(event => event.meta.type !== EventType.UNASSIGNED_DRIVING_LESSON);
            this.refreshView();
            this.displayUnassignedDrivingLessons(this.calendarService.fetchUnassignedDrivingLessons());
        });
    }

    startDragToCreate(segment: WeekViewHourSegment, mouseDownEvent: MouseEvent, segmentElement: HTMLElement) {
        if (!this.calendarService.isTeacherView() && !this.calendarService.isAdminView()) {
            return;
        }

        const dragToSelectEvent: CalendarEvent = {
            id: this.claimId(),
            title: '...',
            start: segment.date,
            meta: {
                tmpEvent: true,
                fresh: true,
                type: EventType.FRESH
            },
            color:
                {
                    primary: "#ff0000",
                    secondary: "#ffffff"
                }
        };

        this.events = [...this.events, dragToSelectEvent];

        const segmentPosition = segmentElement.getBoundingClientRect();
        this.dragToCreateActive = true;
        const endOfView = endOfWeek(this.viewDate, {
            weekStartsOn: this.weekStartsOn
        });

        fromEvent(document, 'mousemove').pipe(
            finalize(() => {
                delete dragToSelectEvent.meta.tmpEvent;
                this.dragToCreateActive = false;
                this.refreshView();
            }),
            takeUntil(fromEvent(document, 'mouseup'))
        ).subscribe((mouseMoveEvent: MouseEvent) => {
            const minutesDiff = ceilToNearestCell((mouseMoveEvent.clientY - segmentPosition.top) / 2, 15);
            const daysDiff = floorToNearestCell(mouseMoveEvent.clientX - segmentPosition.left, segmentPosition.width) / segmentPosition.width;

            const newEnd = addDays(addMinutes(segment.date, minutesDiff), daysDiff);
            if (newEnd > segment.date && newEnd < endOfView) {
                dragToSelectEvent.end = newEnd;
            }
            this.refreshView();
            this.showSidebar(dragToSelectEvent);
        });
    }

    eventClicked({event, sourceEvent}: { event: CalendarEvent, sourceEvent: MouseEvent | KeyboardEvent }): void {
        this.showSidebar(event);
    }

    changeWeek(date: Date): void {
        this.viewDate = date;
        this.view = CalendarView.Week;
    }

    refreshView() {
        this.refresh.next();
    }

    eventTimesChanged({event, newStart, newEnd}: CalendarEventTimesChangedEvent): void {
        event.start = newStart;
        event.end = newEnd;
        this.refreshView();
    }

    removeEvent(event: CalendarEvent) {
        this.events = this.events.filter(iEvent => iEvent !== event);
    }

    displayTheoryLessons(o: Observable<TheoryLesson[]>): void {
        o.subscribe(
            lessons => {
                lessons.forEach(
                    lesson => {
                        this.events.push({
                            id: this.events.length,
                            meta:
                                {
                                    associated: lesson,
                                    type: EventType.THEORY_LESSON
                                },
                            title: 'Theoriestunde',
                            start: new Date(lesson.begin),
                            end: new Date(lesson.end),
                            color:
                                {
                                    primary: "#6d76ff",
                                    secondary: "#ffffff"
                                }
                        })
                    }
                );
                this.refreshView();
            }
        );
        this.refreshView();
    }

    displayDrivingLessons(o: Observable<DrivingLesson[]>) {
        o.subscribe(
            lessons => {
                lessons.forEach(
                    lesson => {
                        this.events.push(
                            {
                                id: this.claimId(),
                                title: 'Fahrstunde',
                                meta:
                                    {
                                        associated: lesson,
                                        type: EventType.DRIVING_LESSON,
                                    },
                                start: new Date(lesson.begin),
                                end: new Date(lesson.end)
                            }
                        )
                    }
                );
                this.refreshView();
            }
        );
        this.refreshView();
    }

    displayUnassignedDrivingLessons(o: Observable<DrivingLesson[]>) {
        o.subscribe(
            lessons => {
                lessons.forEach(
                    lesson => {
                        this.events.push(
                            {
                                id: this.claimId(),
                                title: 'Freie Fahrstunde',
                                meta:
                                    {
                                        associated: lesson,
                                        type: EventType.UNASSIGNED_DRIVING_LESSON
                                    },
                                start: new Date(lesson.begin),
                                end: new Date(lesson.end),
                                color:
                                    {
                                        primary: "#ff1f00",
                                        secondary: "#ff8b58"
                                    }
                            }
                        );
                    }
                );
                this.refreshView();
            }
        );
        this.refreshView();
    }

    showSidebar(event: CalendarEvent): void {
        this.sidebarOpen.next(true);
        this.sidebarEvent.next(event);
    }

    ngOnInit(): void {
        this.sidebarOpen.next(false);

        /*
        this.registerCloseSidebarListener();
        this.registerEventChangeContentListener();
        this.registerDeleteEventListener();
        this.registerDeleteAllTheoryLessonsListener();
        this.registerDeleteAllDrivingLessonsListener();
        this.registerDeleteAllUnassignedDrivingLessonsListener();
         */

        this.displayTheoryLessons(this.calendarService.fetchTheoryLessons());
        this.displayUnassignedDrivingLessons(this.calendarService.fetchUnassignedDrivingLessons());

        if (this.calendarService.isStudentView()) {
            this.displayDrivingLessons(this.calendarService.fetchDrivingLessons());
        } else if (this.calendarService.isTeacherView()) {
            this.displayDrivingLessons(this.calendarService.fetchDrivingLessons());
        } else if (this.calendarService.isAdminView()) {
            this.displayDrivingLessons(this.calendarService.fetchDrivingLessons());
        } else {
        }

        /*
        this.eventsSubject = this.calendarService.getEvents();
        this.eventsSubject.subscribe(e => {
            console.log("home");
            console.log(e);
            this.events = e;
        }, e => {
            console.error(e);
        });
         */
    }

}

