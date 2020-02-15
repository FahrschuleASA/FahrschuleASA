import {AfterViewInit, Component, ElementRef, Renderer, Input, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';
import {Router} from '@angular/router';
import {CalendarService} from "app/home/calendar-utils/calendar.service";
import {Student} from "app/shared/model/student.model";
import {LocationModalComponent} from "app/home/modals/location/location.modal.component";
import {Location} from "app/shared/model/location.model";

@Component({
    selector: 'book-lesson-modal',
    templateUrl: './book-lesson.modal.component.html',
    providers: [CalendarService]
})
export class BookLessonModalComponent {

    @Input() event;

    eventForm = this.fb.group({
        street: ['']
    });

    student: Student = null;
    pickupIsSet: boolean = false;
    done: boolean = false;

    pickup: Location = null;
    destination: Location = null;

    _isAdminView: boolean = false;
    _isTeacherView: boolean = false;

    constructor(
        private fb: FormBuilder,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager,
        private elementRef: ElementRef,
        private router: Router,
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
    }

    cancel() {
        this.activeModal.dismiss('cancel');
    }

    close() {
        this.activeModal.dismiss('updateEvent');
    }

    openLocationModal(): void {
        console.error("LESSON: ", this.event.meta.associated);
        const modalRef = this.modalService.open(LocationModalComponent);

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

    updateEvent(): void {
        const associated = this.event.meta.associated;
        const lesson = {
            id: associated.id,
            begin: associated.begin,
            end: associated.end,
            pickup: this.pickup,
            destination: this.destination,
            lessonType: associated.lessonType,
            teacherId: associated.teacherId,
            driverId: this.getStudent().user.id,
            bookable: true
        };

        console.error("DRIVING LESSON: ", lesson);

        this.calendarService.bookDrivingLesson(lesson);

        this.deleteEvent();
        this.eventManager.broadcast({
            name: 'deleteAllDrivingLessons'
        });

        this.eventManager.broadcast({
            name: 'closeSidebar'
        });

        this.close();
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

    getStudent() {
        if (this.student == null) {
            this.calendarService.getStudent().subscribe(student => {
                this.student = student
            }, error => {
                this.student = new Student()
            });
        }
        return this.student;
    }
}
