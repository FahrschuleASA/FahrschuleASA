import {AfterViewInit, Component, ElementRef, Input, OnInit, Renderer} from '@angular/core';
import {FormBuilder, FormControl} from '@angular/forms';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';
import {Router} from '@angular/router';
import {CalendarService} from "app/home/calendar-utils/calendar.service";
import {SmallStudent} from "app/shared/model/small-student.model";
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {Teacher} from 'app/shared/model/teacher.model';
import {DrivingLessonType} from "app/shared/model/enumerations/driving-lesson-type.model";
import {DrivingCategory} from "app/shared/model/enumerations/driving-category.model";
import {LocationModalComponent} from "app/home/modals/location/location.modal.component";
import {Location} from "app/shared/model/location.model";
import {EventType} from "app/shared/model/enumerations/event-type.model";
import { CalendarEvent } from 'angular-calendar';
import {DrivingLesson} from "../../../shared/model/driving-lesson.model";
import {DrivingSchoolConfigurationService} from "../../../admin/driving-school-configuration/driving-school-configuration.service";
import { DatePipe } from '@angular/common';

@Component({
    selector: 'calendar-event-modal',
    templateUrl: './driving-lesson.modal.component.html',
})
export class DrivingLessonModalComponent implements OnInit {

    @Input() event;

    eventForm = this.fb.group({
        title: ['']
    });

    autocompleteControlStudent = new FormControl();
    autocompleteControlTeacher = new FormControl();

    student: SmallStudent = null;
    studentOptions: SmallStudent[] = [];
    filteredStudentOptions: Observable<SmallStudent[]>;

    teacher: Teacher = null;
    teacherOptions: Teacher[] = [];
    filteredTeacherOptions: Observable<Teacher[]>;

    drivingLessonTypeOptions: string[] = [];
    drivingLessonTypeString: string = null;

    drivingCategoryOptions: string[] = [];
    drivingCategoryString: string = null;

    bookable: boolean = false;
    assignedLesson: boolean = false;

    _fresh: boolean = false;
    _isAdminView: boolean = false;
    _isTeacherView: boolean = false;

    pickupIsSet: boolean = false;
    pickup: Location = null;
    destination: Location = null;
    done: boolean = false;

    eventStartString: string;
    eventEndString: string;

    constructor(
        private fb: FormBuilder,
        public activeModal: NgbActiveModal,
        private datePipe: DatePipe,
        private eventManager: JhiEventManager,
        private elementRef: ElementRef,
        private router: Router,
        private renderer: Renderer,
        public modalService: NgbModal,
        public calendarService: CalendarService,
        private drivingSchoolConfigurationService: DrivingSchoolConfigurationService
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

        for (const n in DrivingLessonType) {
            this.drivingLessonTypeOptions.push(n);
        }
    }

    ngOnInit(): void {
        this.drivingSchoolConfigurationService.fetchAvailableDrivingCategories().subscribe(
            drivingCategories =>{
                this.drivingCategoryOptions = drivingCategories;
            }
        );
        this.filteredStudentOptions = this.autocompleteControlStudent.valueChanges.pipe(
            startWith(''),
            map(value => this._filterStudents(value))
        );

        this.filteredTeacherOptions = this.autocompleteControlTeacher.valueChanges.pipe(
            startWith(''),
            map(value => this._filterTeachers(value))
        );

        if (this.event.meta.hasOwnProperty('fresh')) {
            this._fresh = true;
        }

        this.eventStartString = this.datePipe.transform(this.event.start,'dd.MM.yyyy, HH:mm');
        this.eventEndString = this.datePipe.transform(this.event.end,'dd.MM.yyyy, HH:mm');

        if (!this._fresh) {
            if (this.event.meta.associated.lessonType) {
                this.drivingLessonTypeString = this.event.meta.associated.lessonType.toString();
            } else {
                this.drivingLessonTypeString = '';
            }

            if (this.event.meta.associated.drivingCategory) {
                this.drivingCategoryString = this.event.meta.associated.drivingCategory.toString();
            } else {
                this.drivingCategoryString = '';
            }

            this.calendarService.getTeacher(this.event.meta.associated.teacherId).subscribe(teacher => {
                this.teacher = teacher;
            });

            if (this.event.meta.type === EventType.DRIVING_LESSON) {
                this.student = this.event.meta.associated.driver;
                this.assignedLesson = true;
                this.destination = this.event.meta.associated.destination;
                this.pickup = this.event.meta.associated.pickup;
            }

            console.error(this.event.meta.associated);
        }
    }

    cancel() {
        if (this.event.meta.hasOwnProperty('pushed')) {
            this.eventManager.broadcast({
                name: 'deleteCalendarEvent',
                content: this.event
            });
            this.broadcastChanges();
        }
        this.activeModal.dismiss('cancel');
    }

    createDrivingLesson(): void {
        let lesson: DrivingLesson;
        if (!this.assignedLesson) {
            lesson = {
                begin: this.event.start,
                bookable: true,
                drivingCategory: DrivingCategory[this.drivingCategoryString],
                end: this.event.end,
                lessonType: DrivingLessonType[this.drivingLessonTypeString],
                teacherId: this.teacher.id
            };
        } else {
            lesson = {
                begin: this.event.start,
                destination: this.destination,
                driver: this.student,
                drivingCategory: DrivingCategory[this.drivingCategoryString],
                end: this.event.end,
                lessonType: DrivingLessonType[this.drivingLessonTypeString],
                pickup: this.pickup,
                teacherId: this.teacher.id
            };
        }

        console.error("LESSON: ", lesson);

        this.calendarService.createDrivingLesson(lesson).subscribe(
            lesson => {
                const event: CalendarEvent = {
                    start: lesson.begin,
                    end: lesson.end,
                    title: 'Fahrstunde'
                };
                console.log("Create event");
                this.calendarService.addEvent(event);
            }
        );

        this.deleteEvent();

        this.broadcastChanges();
        this.activeModal.dismiss('create');
    }

    updateDrivingLesson(): void {
        const lesson = {
            destination: this.destination,
            lessonType: DrivingLessonType[this.drivingLessonTypeString],
            pickup: this.pickup,
            id: this.event.meta.associated.id
        };

        this.calendarService.updateDrivingLesson(lesson);


        this.broadcastChanges();
        this.activeModal.dismiss('update');
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

    selectStudent(student: SmallStudent): void {
        this.student = student;
        this.autocompleteControlStudent.setValue(student.firstname + ' ' + student.lastname);
    }

    selectTeacher(teacher: Teacher): void {
        this.teacher = teacher;
        this.autocompleteControlTeacher.setValue(teacher.user.firstName + ' ' + teacher.user.lastName);
    }

    openLocationModal(): void {
        const modalRef = this.modalService.open(LocationModalComponent);
        modalRef.componentInstance.designatedStudentId = this.student.studentId;

        modalRef.result.then((result) => {
            if (result) {
                if (!this.pickupIsSet) {
                    this.pickup = result;
                } else {
                    this.destination = result;
                    this.done = true;
                }
                this.pickupIsSet = true;
            }
        });
    }
}
