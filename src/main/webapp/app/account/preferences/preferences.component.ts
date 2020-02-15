import {Component, LOCALE_ID, ChangeDetectorRef, Inject} from '@angular/core';
import {CalendarService} from "app/home/calendar-utils/calendar.service";
import {EventType} from "app/shared/model/enumerations/event-type.model";
import {
    CalendarView,
    CalendarEvent,
    DAYS_OF_WEEK,
    CalendarEventTitleFormatter,
    CalendarDateFormatter
} from 'angular-calendar';
import {Subject, Observable} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {CustomEventTitleFormatter} from "app/home/calendar-utils/eventtitle.formatter";
import {Preference} from "app/shared/model/preference.model";
import {TimeSlot} from "app/shared/model/time-slot.model";
import {DayOfWeek} from "app/shared/model/enumerations/day-of-week.model";
import {CustomDateFormatterProvider} from "app/account/preferences/calendar-utils/custom-date-formatter.provider";
import {Student} from "app/shared/model/student.model";
import {Location} from "app/shared/model/location.model";
import {DoubleLocationModalComponent} from "app/account/preferences/modals/double-location.modal.component";

@Component({
    selector: 'jhi-preferences',
    templateUrl: './preferences.component.html',
    styleUrls: ['./preferences.component.scss'],
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
export class PreferencesComponent {
    view: CalendarView = CalendarView.Week;

    viewDate: Date = new Date();

    events: CalendarEvent[] = [];

    locale: string = this.localeId;

    weekStartsOn: DAYS_OF_WEEK.MONDAY = DAYS_OF_WEEK.MONDAY;
    weekendDays: number[] = [DAYS_OF_WEEK.SUNDAY];

    refresh: Subject<any> = new Subject<any>();

    count: 0 = 0;

    weekRef: number = 0;

    student: Student = null;
    pickup: Location = null;
    destination: Location = null;

    timeSlots: TimeSlot[] = [];

    alreadyClicked: boolean = false;

    constructor(
        private cdr: ChangeDetectorRef,
        private calendarService: CalendarService,
        @Inject(LOCALE_ID) private localeId: string,
        private modalService: NgbModal
    ) {
        this.parseWeekRef();
    }

    claimId(): number {
        this.count++;
        return this.count;
    }

    eventClicked({event, sourceEvent}: { event: CalendarEvent, sourceEvent: MouseEvent | KeyboardEvent }): void {
        if (event.meta.clicked) {
            this.alreadyClicked = true;
            return;
        }

        if (event.meta.preferenced) {
            event.color.primary = "#FF0000";
            event.color.secondary = "#FFFFFF";
            event.meta.preferenced = false;

            this.calendarService.deletePreference(event.meta.associated);
            event.meta.associated = null;
            event.meta.clicked = true;
        } else {
            let success = true;
            let modalRef = this.modalService.open(DoubleLocationModalComponent);
            modalRef.componentInstance.designatedStudentId = this.student.id;

            modalRef.result.then(
                (result) => {
                    if (result) {
                        this.pickup = result.pickup;
                        this.destination = result.destination;

                        const preference = {
                            destination: this.destination,
                            pickup: this.pickup,
                            studentId: this.student.id,
                            timeSlotId: event.meta.timeslotId
                        };
                        this.calendarService.createPreference(preference);

                        event.color.primary = "#ff0000";
                        event.color.secondary = "#cfff81";
                        event.meta.preferenced = true;
                        event.meta.clicked = true;
                    }
                },
                (error) => {
                    success = false;
                    console.error(error);
                });
        }
        this.refreshView();
    }

    refreshView() {
        this.refresh.next();
    }

    removeEvent(event: CalendarEvent) {
        this.events = this.events.filter(iEvent => iEvent !== event);
    }

    removeAllEvents() {
        this.events = [];
    }

    display(oSlots: Observable<TimeSlot[]>, oPreferences: Observable<Preference[]>): void {
        oSlots.subscribe(slots => {
            slots.forEach(slot => {
                this.timeSlots.push(slot);
                const dates = this.parseSlotDate(slot);
                this.events.push({
                    id: this.claimId(),
                    title: '',
                    start: dates.start,
                    end: dates.end,
                    meta: {
                        preferenced: false,
                        type: EventType.TIME_SLOT,
                        timeslotId: slot.id,
                        associated: null,
                        clicked: false
                    },
                    color: {
                        primary: "#000000",
                        secondary: "#ffffff"
                    }
                });
            });

            oPreferences.subscribe(preferences => {
                preferences.forEach(preference => {
                    this.events.forEach(event => {
                        if (event.meta.timeslotId === preference.timeSlotId) {
                            event.meta.preferenced = true;
                            event.color.primary = "#000000";
                            event.color.secondary = "#cfff81";
                            event.meta.associated = preference;
                        }
                    });
                });
                this.refreshView();
            });
        });
    }

    private parseSlotDate(slot: TimeSlot): { start: Date, end: Date } {
        const start = new Date(this.weekRef + PreferencesComponent.parseDayOfWeek(slot.day) * 86400000 + slot.begin * 60000);
        const end = new Date(this.weekRef + PreferencesComponent.parseDayOfWeek(slot.day) * 86400000 + slot.end * 60000);
        return {start: new Date(start), end: new Date(end)};
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

    private parseWeekRef() {
        const date = new Date();
        const newDate = new Date();
        newDate.setHours(0);
        newDate.setMinutes(0);
        newDate.setSeconds(0);
        newDate.setMilliseconds(0);
        newDate.setDate(date.getDate() - ((date.getDay() + 6) % 7));
        this.weekRef = newDate.getTime();
    }

    ngOnInit(): void {
        this.parseWeekRef();
        this.display(this.calendarService.fetchTimeSlots(), this.calendarService.fetchPreferences());

        this.calendarService.getStudent().subscribe(student => this.student = student);
    }
}
