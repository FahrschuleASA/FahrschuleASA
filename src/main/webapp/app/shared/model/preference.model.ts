import {Location} from "app/shared/model/location.model";

export interface IPreference {
    id?: number;
    pickup?: Location;
    destination?: Location;
    timeSlotId?: number;
    studentId?: number;
}

export class Preference implements IPreference {

    constructor(
        public id?: number,
        public destination?: Location,
        public pickup?: Location,
        public studentId?: number,
        public timeSlotId?: number
    ) {
    }

}
