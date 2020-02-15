import {Component, Input, Output, EventEmitter} from '@angular/core';
import {CalendarView} from 'angular-calendar';

@Component({
    selector: 'jhi-mwl-custom-calendar-header',
    template: `
        <div class="row text-center">

            <div class="col-md-4">
                <div class="btn-group">
                    <div
                        class="btn btn-primary"
                        (click)="viewChange.emit('month')"
                        [class.active]="view === 'month'"
                    >
                        Monat
                    </div>
                    <div
                        class="btn btn-primary"
                        (click)="viewChange.emit('week')"
                        [class.active]="view === 'week'"
                    >
                        Woche
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <h3>{{ viewDate | calendarDate: view + 'ViewTitle':locale }}</h3>
            </div>

            <div class="col-md-4">
                <div class="btn-group">
                    <div
                        class="btn btn-primary"
                        mwlCalendarPreviousView
                        [view]="view"
                        [(viewDate)]="viewDate"
                        (viewDateChange)="viewDateChange.next(viewDate)"
                    >
                        <span>&#8592;</span>
                    </div>
                    <div
                        class="btn btn-outline-secondary"
                        mwlCalendarToday
                        [(viewDate)]="viewDate"
                        (viewDateChange)="viewDateChange.next(viewDate)"
                    >
                        Heute
                    </div>
                    <div
                        class="btn btn-primary"
                        mwlCalendarNextView
                        [view]="view"
                        [(viewDate)]="viewDate"
                        (viewDateChange)="viewDateChange.next(viewDate)"
                    >
                        <span>&#8594;</span>
                    </div>
                </div>
            </div>
        </div>
        <br/>
    `
})
export class CalendarHeaderComponent {
    @Input() view: CalendarView | 'month' | 'week' | 'day';

    @Input() viewDate: Date;

    @Input() locale = 'de';

    @Output() viewChange: EventEmitter<string> = new EventEmitter();

    @Output() viewDateChange: EventEmitter<Date> = new EventEmitter();
}
