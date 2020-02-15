import {AfterViewInit, Component, ElementRef, Renderer, Input, OnInit} from '@angular/core';
import {FormBuilder, FormControl} from '@angular/forms';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';
import {Router} from '@angular/router';
import {CalendarService} from "app/home/calendar-utils/calendar.service";
import {SmallStudent} from "app/shared/model/small-student.model";
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {Teacher} from 'app/shared/model/teacher.model';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'calendar-event-modal',
    templateUrl: './theory-lesson.modal.component.html',
    providers: [CalendarService],
    styleUrls: ['theory-lesson.modal.scss']
})
export class TheoryLessonModalComponent implements OnInit, AfterViewInit {

    @Input() event;

    eventForm = this.fb.group({
        title: ['']
    });

    autocompleteControlStudent = new FormControl();
    autocompleteControlTeacher = new FormControl();

    students: SmallStudent[] = [];
    studentOptions: SmallStudent[] = [];
    filteredStudentOptions: Observable<SmallStudent[]>;
    deletedStudents: SmallStudent[] = [];
    addedStudents: SmallStudent[] = [];

    teacher: Teacher = null;
    teacherOptions: Teacher[] = [];
    filteredTeacherOptions: Observable<Teacher[]>;

    subject: string = '';

    _fresh: boolean = false;
    _isAdminView: boolean = false;
    _isTeacherView: boolean = false;

    eventStartString: string;
    eventEndString: string;

    constructor(
        private fb: FormBuilder,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager,
        private elementRef: ElementRef,
        private router: Router,
        private datePipe: DatePipe,
        private renderer: Renderer,
        public modalService: NgbModal,
        private calendarService: CalendarService
    ) {

        this._isTeacherView = false;
        this._isAdminView = false;
        if (this.calendarService.isTeacherView()) {
            this._isTeacherView = true;
        } else if (this.calendarService.isAdminView()) {
            this._isAdminView = true;
        }

        if (this._isAdminView) {
            this.calendarService.getAllTeachers().subscribe(teachers => {
                teachers.forEach(teacher => {
                    this.teacherOptions.push(teacher);
                });
            });
        }

        if (this._isTeacherView) {
            this.calendarService.getTeacher().subscribe(teacher => {
                this.teacher = teacher;
            })
        }

        this.calendarService.getAllStudentsSmall().subscribe(students => {
            students.forEach(student => {
                this.studentOptions.push(student);
            });
        });
    }

    ngOnInit(): void {
        this.filteredStudentOptions = this.autocompleteControlStudent.valueChanges.pipe(
            startWith(''),
            map(value => this._filterStudents(value))
        );

        this.eventStartString = this.datePipe.transform(this.event.start,'dd.MM.yyyy, HH:mm');
        this.eventEndString = this.datePipe.transform(this.event.end,'dd.MM.yyyy, HH:mm');

        this.filteredTeacherOptions = this.autocompleteControlTeacher.valueChanges.pipe(
            startWith(''),
            map(value => this._filterTeachers(value))
        );

        if (this.event.meta.hasOwnProperty('fresh')) {
            this._fresh = true;
        }

        if (!this._fresh) {
            this.subject = this.event.meta.associated.subject;
            this.students = this.event.meta.associated.students;
        }
    }

    ngAfterViewInit(): void {
        setTimeout(() => this.renderer.invokeElementMethod(this.elementRef.nativeElement.querySelector('#title'), 'focus', []), 0);
    }

    cancel() {
        this.eventManager.broadcast({
            name: 'deleteSidebarEvent',
        });
        this.closeSidebar();
        this.broadcastChanges();

        this.activeModal.dismiss('cancel');
    }

    createTheoryLesson(): void {
        const lesson = {
            begin: this.event.start,
            end: this.event.end,
            subject: this.eventForm.get('title').value,
            teacherId: this.teacher.id
        };

        this.calendarService.createTheoryLesson(lesson);

        this.deleteEvent();
        this.eventManager.broadcast({
            name: 'deleteAllTheoryLessons'
        });


        this.broadcastChanges();
        this.closeSidebar();
        this.activeModal.dismiss('create');
    }

    updateTheoryLesson(): void {
        if (this.eventForm.get('title').value !== '') {
            this.event.meta.associated.subject = this.eventForm.get('title').value;
            this.calendarService.changeTheoryLessonSubject(this.event.meta.associated);
        }

        this.deletedStudents.forEach(student => {
            this.addedStudents.filter(val => val !== student);
        });
        if (this.addedStudents !== []) {
            this.calendarService.addStudentsToTheoryLesson(this.event.meta.associated, this.addedStudents);
        }
        if (this.deletedStudents !== []) {
            this.calendarService.removeStudentsFromTheoryLesson(this.event.meta.associated, this.deletedStudents);
        }

        this.deleteEvent();
        this.eventManager.broadcast({
            name: 'deleteAllTheoryLessons'
        });

        this.broadcastChanges();
        this.closeSidebar();
        this.activeModal.dismiss('update');
    }

    deleteTheoryLesson(): void {
        this.calendarService.deleteTheoryLesson(this.event.meta.associated);
        this.deleteEvent();
    }

    deleteEvent(): void {
        this.broadcastChanges();
        this.eventManager.broadcast({
            name: 'deleteCalendarEvent',
            content: this.event
        });
        this.activeModal.dismiss();
    }

    broadcastChanges(): void {
        this.eventManager.broadcast({
            name: 'refreshCalendarView'
        });
    }

    closeSidebar(): void {
        this.eventManager.broadcast({
            name: 'closeSlideMenuEvent'
        });
    }

    private _filterStudents(value: String): SmallStudent[] {
        if (typeof value !== 'string') {
            return [];
        }
        const filterValue = value.toLowerCase();
        return this.studentOptions.filter(option => (option.firstname + ' ' + option.lastname).toLowerCase().includes(filterValue));
    }

    private _filterTeachers(value: String): Teacher[] {
        if (typeof value !== 'string') {
            return [];
        }
        const filterValue = value.toLowerCase();
        return this.teacherOptions.filter(option => (option.user.firstName + ' ' + option.user.lastName).toLowerCase().includes(filterValue));
    }

    switchStudentStatus(val: SmallStudent): void {
        if (this.deletedStudents.includes(val)) {
            this.deletedStudents = this.deletedStudents.filter(student => student !== val);
        } else {
            this.deletedStudents.push(val);
        }
    }

    addStudent(student: SmallStudent): void {
        if (!this.addedStudents.includes(student) && !this.students.includes(student)) {
            this.addedStudents.push(student);
            this.students.push(student);
        }
        this.autocompleteControlStudent.setValue('');
    }

    selectTeacher(teacher: Teacher): void {
        this.teacher = teacher;
        this.autocompleteControlTeacher.setValue(teacher.user.firstName + ' ' + teacher.user.lastName);
    }
}
