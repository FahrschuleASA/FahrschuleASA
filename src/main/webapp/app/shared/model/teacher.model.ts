import {IStudent} from 'app/shared/model/student.model';
import {ITimeSlot} from 'app/shared/model/time-slot.model';
import {ITheoryLesson} from 'app/shared/model/theory-lesson.model';
import {Address} from "./address.model";
import {IUser} from "../../core/user/user.model";

export interface ITeacher {
    id?: number;
    changedTimeSlots?: boolean;
    students?: IStudent[];
    timeSlots?: ITimeSlot[];
    theoryLessons?: ITheoryLesson[];
    schoolOwner?: boolean;
    birthdate?: Date;
    address?: Address;
    phoneNumber?: string;
    user?: IUser;
    active?: boolean;
    deactivatedDaysLeft?: number;
    newEmail?: string;
}

export class Teacher implements ITeacher {

    constructor(
        public id?: number,
        public active?: boolean,
        public address?: Address,
        public birthdate?: Date,
        public changedTimeSlots?: boolean,
        public newEmail?: string,
        public phoneNumber?: string,
        public deactivatedDaysLeft?: number,
        public schoolOwner?: boolean,
        public students?: IStudent[],
        public theoryLessons?: ITheoryLesson[],
        public timeSlots?: ITimeSlot[],
        public user?: IUser,
    ) {
    }
}
