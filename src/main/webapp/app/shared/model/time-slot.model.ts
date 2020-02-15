import {DrivingCategory} from "app/shared/model/enumerations/driving-category.model";
import {DayOfWeek} from "app/shared/model/enumerations/day-of-week.model";

export interface ITimeSlot {
    id?: number,
    begin?: number,
    end?: number,
    day?: DayOfWeek,
    blockedDates?: Date[],
    teacherId?: number,
    preferredCategories?: DrivingCategory[],
    optionalCategories?: DrivingCategory[],
}

export class TimeSlot implements ITimeSlot {

    constructor(
        public id?: number,
        public begin?: number,
        public end?: number,
        public day?: DayOfWeek,
        public teacherId?: number,
        public blockedDates?: Date[],
        public optionalCategories?: DrivingCategory[],
        public preferredCategories?: DrivingCategory[],
    ) {
    }
}
