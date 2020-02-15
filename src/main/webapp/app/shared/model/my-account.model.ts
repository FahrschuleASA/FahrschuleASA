import {IUser} from 'app/core/user/user.model';
import {Address} from "./address.model";

export interface IMyAccount {
    id?: number;
    active?: boolean;
    birthdate?: Date;
    address?: Address;
    phoneNumber?: string;
    newEmail?: string;
    user?: IUser;
    deactivatedDaysLeft?: number;
}

export class MyAccount implements IMyAccount {
    constructor(
        public id?: number,
        public active?: boolean,
        public birthdate?: Date,
        public address?: Address,
        public phoneNumber?: string,
        public newEmail?: string,
        public user?: IUser,
        public deactivatedDaysLeft?: number
    ) {
        this.active = this.active || false;
    }
}
