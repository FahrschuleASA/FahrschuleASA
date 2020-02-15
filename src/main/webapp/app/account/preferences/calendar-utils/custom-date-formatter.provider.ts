import {Injectable} from '@angular/core';
import {CalendarDateFormatter, DateFormatterParams} from 'angular-calendar';
import { DatePipe } from '@angular/common';

@Injectable()
export class CustomDateFormatterProvider extends CalendarDateFormatter {
    public weekViewColumnHeader({date, locale}: DateFormatterParams): string {
        return new DatePipe(locale).transform(date, 'EEEE', locale);
    }

    public weekViewColumnSubHeader({date, locale}: DateFormatterParams): string {
        return "";
    }

    weekViewHour({date, locale}: DateFormatterParams): string {
        return new DatePipe(locale).transform(date, 'HH:mm', locale);
    }
}
