import {ChangeDetectorRef, Component, Inject, LOCALE_ID} from '@angular/core';
import {CalendarService} from "app/home/calendar-utils/calendar.service";
import {
    CalendarDateFormatter,
    CalendarEvent,
    CalendarEventTitleFormatter,
    CalendarView,
    DAYS_OF_WEEK
} from 'angular-calendar';
import {Observable, Subject, fromEvent} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {CustomEventTitleFormatter} from "app/home/calendar-utils/eventtitle.formatter";
import {CustomDateFormatterProvider} from "app/account/time-slots/calendar-utils/custom-date-formatter.provider";
import {TimeSlot} from "app/shared/model/time-slot.model";
import {DayOfWeek} from "app/shared/model/enumerations/day-of-week.model";
import {EventType} from "app/shared/model/enumerations/event-type.model";
import {TimeSlotModalComponent} from "app/account/time-slots/modals/time-slot.modal.component";
import {JhiEventManager} from 'ng-jhipster';
import {DrivingCategory} from "app/shared/model/enumerations/driving-category.model";
import {DatePipe} from '@angular/common';
import {WeekViewHourSegment} from 'calendar-utils';
import {endOfWeek, addDays, addMinutes} from 'date-fns';
import {finalize, takeUntil} from 'rxjs/operators';

function floorToNearestCell(amount: number, precision: number) {
    return Math.floor(amount / precision) * precision;
}

function ceilToNearestCell(amount: number, precision: number) {
    return Math.ceil(amount / precision) * precision;
}

@Component({
    selector: 'jhi-time-slots',
    templateUrl: './time-slots.component.html',
    styleUrls: ['./time-slots.component.scss'],
    providers: [
        CalendarService,
        {
            provide: CalendarEventTitleFormatter,
            useClass: CustomEventTitleFormatter
        },
        {
            provide: CalendarDateFormatter,
            useClass: CustomDateFormatterProvider
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
    ]
})
export class TimeSlotsComponent {
    view: CalendarView = CalendarView.Week;

    viewDate: Date = new Date();

    events: CalendarEvent[] = [];

    locale: string = this.localeId;

    weekStartsOn: DAYS_OF_WEEK.MONDAY = DAYS_OF_WEEK.MONDAY;
    weekendDays: number[] = [DAYS_OF_WEEK.SUNDAY];

    refresh: Subject<any> = new Subject<any>();

    count: 0 = 0;

    weekRef: number = 0;

    dragToCreateActive: boolean = false;

    constructor(
        private cdr: ChangeDetectorRef,
        private calendarService: CalendarService,
        @Inject(LOCALE_ID) private localeId: string,
        private modalService: NgbModal,
        private eventManager: JhiEventManager,
        private datepipe: DatePipe
    ) {
        this.parseDateTwoWeeksAhead();
    }

    claimId(): number {
        this.count++;
        return this.count;
    }

    registerUpdateListener() {
        this.eventManager.subscribe('updateTimeSlotEvent', ({name, content}) => {
            this.updateCalendarEvent(content);
        });
        this.refreshView();
    }

    registerEventQueueListener() {
        this.eventManager.subscribe('queueTimeSlotChangeEvent', ({name, content}) => {

        });
        this.refreshView();
    }

    registerAddEventListener() {
        this.eventManager.subscribe('addTimeSlotEvent', ({name, content}) => {
            this.events.push(content);
            this.refreshView();
        });
    }

    registerDeleteEventListener() {
        this.eventManager.subscribe('deleteTimeSlotEvent', ({name, content}) => {
            this.events = this.events.filter(iEvent => iEvent !== content);
            this.refreshView();
        });
    }

    updateCalendarEvent(event: CalendarEvent): void {
        this.events.forEach(val => {
            if (val.meta.associated.id === event.meta.associated.id) {
                val.meta.associated = event.meta.associated;
            }
        });
    }

    eventClicked({event, sourceEvent}: { event: CalendarEvent, sourceEvent: MouseEvent | KeyboardEvent }): void {
        if (event.meta.type === EventType.FRESH) {
            const ref = this.modalService.open(TimeSlotModalComponent);
            ref.componentInstance.event = event;
            ref.componentInstance.weekRef = this.weekRef;
        } else {
            const ref = this.modalService.open(TimeSlotModalComponent);
            ref.componentInstance.event = event;
        }
    }

    refreshView() {
        this.refresh.next();
    }

    displayTimeSlots(o: Observable<TimeSlot[]>): void {
        o.subscribe(slots => {
            slots.forEach(slot => {
                const dates = this.parseSlotDate(slot);
                dates.forEach(date => {
                    this.events.push({
                        title: slot.preferredCategories.toString() + ' (' + slot.optionalCategories.toString() + ')',
                        id: this.claimId(),
                        meta: {
                            type: EventType.TIME_SLOT,
                            associated: slot,
                            blocked: slot.blockedDates.includes(date.simplified)
                        },
                        start: date.start,
                        end: date.end,
                        color: {
                            primary: !slot.blockedDates.includes(date.simplified) ? "#000000" : "#FFFFFF",
                            secondary: !slot.blockedDates.includes(date.simplified) ? "#FFFFFF" : "#FF0000"
                        }
                    });
                });
            });
            this.refreshView();
        });
        this.refreshView();
    }

    private parseSlotDate(slot: TimeSlot): { start: Date, end: Date, simplified: Date }[] {
        const start = this.weekRef + TimeSlotsComponent.parseDayOfWeek(slot.day) * 86400000 + slot.begin * 60000;
        const end = this.weekRef + TimeSlotsComponent.parseDayOfWeek(slot.day) * 86400000 + slot.end * 60000;
        let res = [];
        for (let i = 0; i < 4; i++) {
            const ref = new Date(start + i * 604800000);
            res.push({
                start: ref,
                end: new Date(end + i * 604800000),
                simplified: this.datepipe.transform(ref, 'yyyy-MM-dd')
            });
        }
        return res;
    }

    private static parseDayOfWeek(day: DayOfWeek): number {
        switch (day) {
            case DayOfWeek.MO:
                return 0;
            case DayOfWeek.TU:
                return 1;
            case DayOfWeek.WE:
                return 2;
            case DayOfWeek.TH:
                return 3;
            case DayOfWeek.FR:
                return 4;
            case DayOfWeek.SA:
                return 5;
            case DayOfWeek.SU:
                return 6;
        }
    }

    private parseDateTwoWeeksAhead() {
        const date = new Date();
        const newDate = new Date();
        newDate.setHours(0);
        newDate.setMinutes(0);
        newDate.setSeconds(0);
        newDate.setMilliseconds(0);
        newDate.setDate(date.getDate() - ((date.getDay() + 6) % 7));
        this.weekRef = newDate.getTime() + 1209600000;
        this.viewDate = new Date(this.weekRef);
    }

    notifyStudents(): void {
        this.calendarService.notifyStudents();
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
        });
    }

    ngOnInit(): void {
        this.registerUpdateListener();
        this.registerAddEventListener();
        this.registerDeleteEventListener();

        this.displayTimeSlots(this.calendarService.fetchTimeSlots());
    }
}
