export interface ITeachingDiagram {
    id?: number;
    basic?: number;
    advanced?: number;
    performance?: number;
    independence?: number;
    overland?: number;
    autobahn?: number;
    night?: number;
    autobahnCount?: number;
    basicCount?: number;
    nightCount?: number;
    overlandCount?: number;

    drivingLessonsCount?: number;
    missedDrivingLessonsCount?: number;
    lateMissedDrivingLessonsCount?: number;
    theoryLessonsCount?: number;
    drivingCategory?: string;
    readyForTheory?: boolean;
    missionAccomplished?: boolean;
    wantedLessons?: number;
    allowedLessons?: number;
    teacherId?: number;
}

export class TeachingDiagram implements ITeachingDiagram {
    constructor(
        public id?: number,
        public basic?: number,
        public advanced?: number,
        public performance?: number,
        public independence?: number,
        public overland?: number,
        public autobahn?: number,
        public night?: number,
        public autobahnCount?: number,
        public basicCount?: number,
        public nightCount?: number,
        public overlandCount?: number,

        public drivingLessonsCount?: number,
        public missedDrivingLessonsCount?: number,
        public lateMissedDrivingLessonsCount?: number,
        public theoryLessonsCount?: number,
        public drivingCategory?: string,
        public readyForTheory?: boolean,
        public missionAccomplished?: boolean,
        public wantedLessons?: number,
        public allowedLessons?: number,
        public teacherId?: number
    ) {
    }
}
