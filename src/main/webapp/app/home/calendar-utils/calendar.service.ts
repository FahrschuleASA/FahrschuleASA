import {Injectable} from '@angular/core';
import {AccountService} from "app/core/auth/account.service";
import {Observable, Subject, BehaviorSubject} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {SERVER_API_URL} from "app/app.constants";
import {TheoryLesson} from "app/shared/model/theory-lesson.model";
import {DrivingLesson} from "app/shared/model/driving-lesson.model";
import {TimeSlot} from "app/shared/model/time-slot.model";
import {Preference} from "app/shared/model/preference.model";
import {Student} from "app/shared/model/student.model";
import {SmallStudent} from "app/shared/model/small-student.model";
import {Teacher} from "app/shared/model/teacher.model";
import {DatePipe} from "@angular/common";
import {CalendarEvent} from 'angular-calendar';


@Injectable()
export class CalendarService {
    constructor(
        private accountService: AccountService,
        private http: HttpClient,
        private datepipe: DatePipe
    ) {
    }

    private events: CalendarEvent[] = [];
    private eventsSubject: BehaviorSubject<CalendarEvent[]> = new BehaviorSubject<CalendarEvent[]>(this.events);


    addEvent(calendarEvent: CalendarEvent) {
        this.events.push(calendarEvent);
        this.eventsSubject.next(this.events);
        console.log("Pushed event");
    }

    getEvents(): BehaviorSubject<CalendarEvent[]> {
        return this.eventsSubject;
    }

    isStudentView() {
        return this.accountService.hasAnyAuthority(['ROLE_STUDENT']);
    }

    isTeacherView() {
        return this.accountService.hasAnyAuthority(['ROLE_TEACHER']);
    }

    isAdminView() {
        return this.accountService.hasAnyAuthority(['ROLE_ADMIN']);
    }

    fetchTheoryLessons(): Observable<TheoryLesson[]> {
        if (this.isStudentView()) {
            return this.http.get<TheoryLesson[]>(SERVER_API_URL + '/api/theory-lessons/student');
        } else if (this.isTeacherView()) {
            return this.http.get<TheoryLesson[]>(SERVER_API_URL + '/api/theory-lessons/teacher');
        } else if (this.isAdminView()) {
            return this.http.get<TheoryLesson[]>(SERVER_API_URL + '/api/theory-lessons');
        }
    }

    fetchDrivingLessons(): Observable<DrivingLesson[]> {
        if (this.isStudentView()) {
            return this.http.get<DrivingLesson[]>(SERVER_API_URL + '/api/driving-lesson/student');
        } else if (this.isTeacherView()) {
            return this.http.get<DrivingLesson[]>(SERVER_API_URL + '/api/driving-lesson/teacher');
        } else if (this.isAdminView()) {
            return this.http.get<DrivingLesson[]>(SERVER_API_URL + '/api/driving-lessons')
        }
    }

    fetchUnassignedDrivingLessons(): Observable<DrivingLesson[]> {
        return this.http.get<DrivingLesson[]>(SERVER_API_URL + '/api/driving-lesson/unassigned');
    }

    fetchTimeSlots(): Observable<TimeSlot[]> {
        if (this.isStudentView()) {
            return this.http.get<TimeSlot[]>(SERVER_API_URL + '/api/time-slots/student');
        } else if (this.isTeacherView()) {
            return this.http.get<TimeSlot[]>(SERVER_API_URL + '/api/time-slots/teacher');
        }
    }

    updateTimeSlot(slot: TimeSlot): void {
        this.http.put<TimeSlot>(SERVER_API_URL + '/api/time-slots', slot).subscribe(() => {
        });
    }

    fetchPreferences(): Observable<Preference[]> {
        return this.http.get<Preference[]>(SERVER_API_URL + '/api/preferences');
    }

    notifyStudents(): void {
        this.http.post(SERVER_API_URL + '/api/time-slots/notify', null).subscribe(() => {
        });
    }

    createPreference(preference: Preference): any {
        this.http.post<Preference>(SERVER_API_URL + '/api/preferences', preference).subscribe(() => {
        });
    }

    deletePreference(preference: Preference): void {
        this.http.delete<Preference>(SERVER_API_URL + '/api/preferences/' + preference.id.toString()).subscribe(() => {
        });
    }

    addBlockedTimeSlot(slot: TimeSlot, date: Date): void {
        const params = new HttpParams().set('time_slot_id', slot.id.toString()).set('blocked_date', this.datepipe.transform(date, 'yyyy-MM-dd'));
        this.http.post(SERVER_API_URL + '/api/time-slots/blocked-dates', null, {params}).subscribe(() => {
        });
    }

    removeBlockedTimeSlot(slot: TimeSlot, date: Date): void {
        const params = new HttpParams().set('time_slot_id', slot.id.toString()).set('blocked_date', date.toString());
        this.http.delete(SERVER_API_URL + '/api/time-slots/blocked-dates', {params}).subscribe(() => {
        });
    }

    createTimeSlot(slot: TimeSlot): void {
        this.http.post<TimeSlot>(SERVER_API_URL + '/api/time-slots', slot).subscribe(() => {
        });
    }

    deleteTimeSlot(slot: TimeSlot): void {
        this.http.delete<TimeSlot>(SERVER_API_URL + '/api/time-slots/' + slot.id.toString()).subscribe(() => {
        });
    }

    cancelDrivingLesson(lesson: DrivingLesson, bookable: boolean): void {
        const params = new HttpParams().set('bookable', bookable.toString()).set('driving_lesson_id', lesson.id.toString());
        this.http.put<DrivingLesson>(SERVER_API_URL + '/api/driving-lesson/cancel', null, {params}).subscribe(() => {
        });
    }

    bookDrivingLesson(lesson: DrivingLesson): void {
        this.http.put<DrivingLesson>(SERVER_API_URL + '/api/driving-lesson/book', lesson).subscribe(() => {
        });
    }

    createDrivingLesson(lesson: DrivingLesson): Observable<DrivingLesson> {
        return this.http.post<DrivingLesson>(SERVER_API_URL + '/api/driving-lessons', lesson);
    }

    updateDrivingLesson(lesson: DrivingLesson): void {
        this.http.put<DrivingLesson>(SERVER_API_URL + '/api/driving-lessons', lesson).subscribe(() => {
        });
    }

    createTheoryLesson(lesson: TheoryLesson): void {
        this.http.post<TheoryLesson>(SERVER_API_URL + '/api/theory-lessons', lesson).subscribe(() => {
        });
    }

    changeTheoryLessonSubject(lesson: TheoryLesson): void {
        const params = new HttpParams().set('lessonId', lesson.id.toString()).set('subject', lesson.subject.toString());
        this.http.put<TheoryLesson>(SERVER_API_URL + '/api/theory-lessons/change-subject', null, {params}).subscribe(() => {
        });
    }

    deleteTheoryLesson(lesson: TheoryLesson): void {
        this.http.delete<TheoryLesson>(SERVER_API_URL + '/api/theory-lessons/' + lesson.id.toString()).subscribe(() => {
        });
    }

    addStudentsToTheoryLesson(lesson: TheoryLesson, students: SmallStudent[]): void {
        students.forEach(student => {
            const params = new HttpParams().set('lessonId', lesson.id.toString()).set('studentId', student.studentId.toString());
            this.http.put<TheoryLesson>(SERVER_API_URL + '/api/theory-lessons/add-student', null, {params}).subscribe(() => {
            });
        });
    }

    removeStudentsFromTheoryLesson(lesson: TheoryLesson, students: SmallStudent[]): void {
        students.forEach(student => {
            const params = new HttpParams().set('lessonId', lesson.id.toString()).set('studentId', student.studentId.toString());
            this.http.put<TheoryLesson>(SERVER_API_URL + '/api/theory-lessons/remove-student', null, {params}).subscribe(() => {
            });
        });
    }

    getStudent(id?: number): Observable<Student> {
        if (this.isStudentView()) {
            return this.http.get<Student>(SERVER_API_URL + '/api/student');
        } else {
            if (id === undefined) {
                return;
            }
            return this.http.get<Student>(SERVER_API_URL + '/api/students/' + id);
        }
    }

    getAllStudents(): Observable<Student[]> {
        return this.http.get<Student[]>(SERVER_API_URL + '/api/students');
    }

    getAllStudentsSmall(): Observable<SmallStudent[]> {
        return this.http.get<SmallStudent[]>(SERVER_API_URL + '/api/students/small');
    }

    getTeacher(id?: number): Observable<Teacher> {
        if (this.isTeacherView()) {
            return this.http.get<Teacher>(SERVER_API_URL + '/api/teacher');
        } else if (this.isAdminView()) {
            if (id === undefined) {
                return;
            }
            return this.http.get<Teacher>(SERVER_API_URL + '/api/teachers/' + id);
        }
    }

    getAllTeachers(): Observable<Teacher[]> {
        return this.http.get<Teacher[]>(SERVER_API_URL + '/api/teachers');
    }
}
