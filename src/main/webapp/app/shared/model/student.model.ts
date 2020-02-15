import {ITeachingDiagram} from 'app/shared/model/teaching-diagram.model';
import {IPreference} from 'app/shared/model/preference.model';
import {ITheoryLesson} from 'app/shared/model/theory-lesson.model';
import {IDrivingLesson} from 'app/shared/model/driving-lesson.model';
import {ITeacher} from 'app/shared/model/teacher.model';
import {DrivingCategory} from 'app/shared/model/enumerations/driving-category.model';
import {IUser} from "../../core/user/user.model";
import {Address} from "./address.model";

export interface IStudent {
    id?: number;
    category?: DrivingCategory;
    readyForTheory?: boolean;
    wantedLessons?: number;
    allowedLessons?: number;
    changedPreferences?: boolean;
    teachingDiagram?: ITeachingDiagram;
    preferences?: IPreference[];
    theoryLessons?: ITheoryLesson[];
    optionalDrivingLessons?: IDrivingLesson[];
    teacher?: ITeacher;
    drivingLessons?: IDrivingLesson[];
    missedLessons?: IDrivingLesson[];
    active?: boolean;
    deactivatedDaysLeft?: number;
    teacherId?: number;
    birthdate?: Date;
    notifyForFreeLesson?: boolean;
    address?: Address;
    phoneNumber?: string;
    user?: IUser;
    canceledLessons?: number;
    lateCanceledLessons?: number;
    newEmail?: string;
}

export class Student implements IStudent {

    constructor(
        public id?: number,
        public category?: DrivingCategory,
        public active?: boolean,
        public canceledLessons?: number,
        public readyForTheory?: boolean,
        public address?: Address,
        public allowedLessons?: number,
        public birthdate?: Date,
        public changedPreferences?: boolean,
        public drivingLessons?: IDrivingLesson[],
        public lateCanceledLessons?: number,
        public missedLessons?: IDrivingLesson[],
        public newEmail?: string,
        public optionalDrivingLessons?: IDrivingLesson[],
        public notifyForFreeLesson?: boolean,
        public phoneNumber?: string,
        public deactivatedDaysLeft?: number,
        public preferences?: IPreference[],
        public teacher?: ITeacher,
        public teacherId?: number,
        public teachingDiagram?: ITeachingDiagram,
        public theoryLessons?: ITheoryLesson[],
        public user?: IUser,
        public wantedLessons?: number
    ) {

    }
}
