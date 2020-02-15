export interface IConfiguration {
    initialLessons?: number,
    planningWeekday?: number,
    planningHour?: number,
    planningMinute?: number,
    maxInactive?: number,
    deadlineMissedLesson?: number,
    availableCategories?: string[],
    allCategories?: string[],
    emailSignature?: string
}

export class Configuration implements IConfiguration {
    constructor(
        public initialLessons?: number,
        public planningWeekday?: number,
        public planningHour?: number,
        public planningMinute?: number,
        public maxInactive?: number,
        public deadlineMissedLesson?: number,
        public availableCategories?: string[],
        public allCategories?: string[],
        public emailSignature?: string
    ) {
    }
}

export class Weekday {
}
