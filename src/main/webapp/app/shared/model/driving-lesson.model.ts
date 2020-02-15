import {Location} from "app/shared/model/location.model";
import {DrivingCategory} from "app/shared/model/enumerations/driving-category.model";
import {DrivingLessonType} from "app/shared/model/enumerations/driving-lesson-type.model";
import {SmallStudent} from "app/shared/model/small-student.model";

export interface IDrivingLesson {
    begin?: Date;
    destination?: Location;
    driver?: SmallStudent;
    driverId?: number;
    drivingCategory?: DrivingCategory
    end?: Date;
    id?: number;
    lessonType?: DrivingLessonType;
    pickup?: Location;
    teacherId?: number;
    bookable?: boolean;
    missingStudents?: SmallStudent[];
    lateMissingStudents?: SmallStudent[];
}

export class DrivingLesson implements IDrivingLesson {
    constructor(
    public begin?: Date,
    public drivingCategory?: DrivingCategory,
    public lessonType?: DrivingLessonType,
    public destination?: Location,
    public driver?: SmallStudent,
    public driverId?: number,
    public end?: Date,
    public id?: number,
    public pickup?: Location,
    public teacherId?: number,
    public missingStudents?: SmallStudent[],
    public missingLateStudents?: SmallStudent[],
    public bookable?: boolean
    ) {}
}
