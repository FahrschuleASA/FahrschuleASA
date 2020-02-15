import {ISmallStudent} from "app/shared/model/small-student.model";

export interface ITheoryLesson {
    id?: number;
    subject?: string;
    teacherId?: number;
    students?: ISmallStudent[];
    begin?: Date;
    end?: Date;
}

export class TheoryLesson implements ITheoryLesson {

    constructor(
        public id?: number,
        public subject?: string,
        public begin?: Date,
        public end?: Date,
        public students?: ISmallStudent[],
        public teacherId?: number,
    ) {
    }
}
