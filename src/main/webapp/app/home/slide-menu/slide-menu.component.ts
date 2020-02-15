import {Component, OnInit, Input, ViewChild, ElementRef, Renderer2, AfterViewChecked, AfterViewInit} from '@angular/core';
import {trigger, transition, style, animate} from '@angular/animations';
import {Subject} from 'rxjs';
import {CalendarEvent} from 'calendar-utils';
import {JhiEventManager} from 'ng-jhipster';
import {CalendarService} from "app/home/calendar-utils/calendar.service";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {LocationModalComponent} from "app/home/modals/location/location.modal.component";
import {EventType} from "app/shared/model/enumerations/event-type.model";
import {SmallStudent} from "app/shared/model/small-student.model";
import {TheoryLessonModalComponent} from "app/home/modals/theory-lesson/theory-lesson.modal.component";
import {TheoryLesson} from "app/shared/model/theory-lesson.model";
import {BookLessonModalComponent} from "app/home/modals/book-lesson/book-lesson.modal.component";
import { DatePipe } from '@angular/common';
import {DrivingLessonModalComponent} from "app/home/modals/driving-lesson/driving-lesson.modal.component";
import {Teacher} from "app/shared/model/teacher.model";

@Component({
    selector: 'slide-menu',
    templateUrl: './slide-menu.component.html',
    styleUrls: ['./slide-menu.component.scss'],
    animations: [
        trigger("slideInOut", [
            transition(":enter", [
                style({transform: "translateY(100%)"}),
                animate("200ms ease-in", style({transform: "translateY(0%)"}))
            ]),
            transition(":leave", [
                style({transform: "translateY(0%)"}),
                animate("200ms ease-in", style({transform: "translateY(100%)"}))
            ])
        ])
    ]
})
export class SlideMenuComponent implements OnInit {

    @Input() shown: Subject<boolean>;
    @Input() sidebarEvent: Subject<CalendarEvent>;
    _shown: boolean;
    _sidebarEvent: CalendarEvent;

    _currentDate: Date = new Date();

    _isDrivingLesson: boolean = false;
    _driver: SmallStudent = null;
    _isUnassignedDrivingLesson: boolean = false;
    _isTheoryLesson: boolean = false;
    _subject: string = null;
    _isFresh: boolean = false;
    _teacher: Teacher = null;

    constructor(
        private eventManager: JhiEventManager,
        public calendarService: CalendarService,
        private modalService : NgbModal,
        public datePipe: DatePipe
    ) {
    }

    ngOnInit() {
        this.registerSlideMenuCloseListener();
        this.registerSlideMenuDeleteListener();

        this.shown.subscribe(data => {
            this._shown = data;
        });
        this.sidebarEvent.subscribe( event => {
            this._sidebarEvent = event;

            this._isDrivingLesson = false;
            this._isTheoryLesson = false;
            this._isUnassignedDrivingLesson = false;
            this._isFresh = false;

            if (event != null) {
                switch (this._sidebarEvent.meta.type) {
                    case EventType.DRIVING_LESSON:
                        this._isDrivingLesson = true;
                        this._driver = this._sidebarEvent.meta.associated.driver;
                        if (this.calendarService.isAdminView()) {
                            this.calendarService.getTeacher(this._sidebarEvent.meta.associated.teacherId).subscribe(teacher => {
                               this._teacher = teacher;
                            });
                        }
                        break;
                    case EventType.UNASSIGNED_DRIVING_LESSON:
                        this._isUnassignedDrivingLesson = true;
                        break;
                    case EventType.THEORY_LESSON:
                        this._isTheoryLesson = true;
                        this._subject = this._sidebarEvent.meta.associated.subject;
                        break;
                    case EventType.FRESH:
                        this._isFresh = true;
                        break;
                }
            }

        }, () => {});

    }

    registerSlideMenuCloseListener(): void {
        this.eventManager.subscribe('closeSlideMenuEvent', () => {
            this.close();
        });
    }

    registerSlideMenuDeleteListener(): void {
        this.eventManager.subscribe('deleteSlideMenuEvent', () => {
            this.deleteCalendarEvent();
        });
    }

    close(): void {
        this.shown.next(false);
        if (this._isFresh) {
            this.deleteCalendarEvent();
        }
    }

    broadcastChanges(): void {
        this.eventManager.broadcast({
            name: 'refreshCalendarView'
        });
    }

    deleteCalendarEvent(): void {
        this.eventManager.broadcast({
            name: 'deleteCalendarEvent',
            content: this._sidebarEvent
        });
    }

    cancelDrivingLesson(bookable: boolean): void {
        if (this._sidebarEvent.meta.hasOwnProperty('associated')) {
            this.calendarService.cancelDrivingLesson(this._sidebarEvent.meta.associated, bookable);
            this.deleteCalendarEvent();
        }

        /*
        this.eventManager.broadcast({
            name: 'deleteAllUnassignedDrivingLessons'
        });
         */
        this.close();
    }

    openBookLessonModal(): void {
        const ref = this.modalService.open(BookLessonModalComponent);
        ref.componentInstance.event = this._sidebarEvent;
    }

    openTheoryLessonModal(): void {
        const ref = this.modalService.open(TheoryLessonModalComponent);
        ref.componentInstance.event = this._sidebarEvent;
    }

    openDrivingLessonModal(): void {
        const ref = this.modalService.open(DrivingLessonModalComponent);
        ref.componentInstance.event = this._sidebarEvent;
    }

}
