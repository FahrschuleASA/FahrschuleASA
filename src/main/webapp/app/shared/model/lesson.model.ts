import { Moment } from 'moment';

export interface ILesson {
  id?: number;
  begin?: Moment;
  end?: Moment;
}

export class Lesson implements ILesson {
  constructor(public id?: number, public begin?: Moment, public end?: Moment) {}
}
