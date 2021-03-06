import {Injectable} from '@angular/core'
import {CalendarDateFormatter, DateFormatterParams} from 'angular-calendar';
import {DatePipe} from '@angular/common';

@Injectable()
export class LocaleDateFormatter extends CalendarDateFormatter {

    weekViewHour({date, locale}: DateFormatterParams): string {
        return new DatePipe(locale).transform(date, 'HH:mm', locale);
    }
}
