import {Injectable, Inject, LOCALE_ID} from '@angular/core'
import {CalendarEventTitleFormatter, CalendarEvent} from 'angular-calendar';
import { DatePipe } from '@angular/common';

@Injectable()
export class CustomEventTitleFormatter extends CalendarEventTitleFormatter {

    constructor(@Inject(LOCALE_ID) private locale : string) {
        super();
    }

    week(event: CalendarEvent<any>, title: string): string {
        return `<b>${new DatePipe(this.locale).transform(
            event.start,
            'HH:mm',
            this.locale
        )}</b><br> ${event.title}`;
    }

}
