export interface ISmallStudent {
    firstname?: string;
    lastname?: string;
    studentId?: number;
}

export class SmallStudent implements ISmallStudent {
    constructor(
        public firstname? : string,
        public lastname?: string,
        public studentId?: number
    ) {
    }
}
