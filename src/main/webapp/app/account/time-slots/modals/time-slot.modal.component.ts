import {Component, ElementRef, Input, OnInit, Renderer} from '@angular/core';
import {FormBuilder, FormControl} from '@angular/forms';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';
import {Router} from '@angular/router';
import {CalendarService} from "app/home/calendar-utils/calendar.service";
import {DrivingCategory} from "app/shared/model/enumerations/driving-category.model";
import {EventType} from "app/shared/model/enumerations/event-type.model";
import {DatePipe} from '@angular/common';
import {Teacher} from 'app/shared/model/teacher.model';
import {DayOfWeek} from "app/shared/model/enumerations/day-of-week.model";
import {DrivingSchoolConfigurationService} from "app/admin/driving-school-configuration/driving-school-configuration.service";

@Component({
    selector: 'time-slot-modal',
    templateUrl: './time-slot.modal.component.html',
    providers: [CalendarService]
})
export class TimeSlotModalComponent implements OnInit {

    @Input() event;
    @Input() weekRef;

    drivingCategoryOptions: string[] = [];
    preferredDrivingCategoryString: string[] = [];
    optionalDrivingCategoryString: string[] = [];

    preferredFormControl = new FormControl();
    optionalFormControl = new FormControl();

    blocked: boolean = false;

    teacher: Teacher = null;

    _fresh: boolean = false;

    form = this.fb.group({
        preferredCategories: [undefined],
        optionalCategories: [undefined]
    });

    constructor(
        private fb: FormBuilder,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager,
        private elementRef: ElementRef,
        private router: Router,
        private renderer: Renderer,
        public modalService: NgbModal,
        private calendarService: CalendarService,
        private datepipe: DatePipe,
        private drivingSchoolConfigurationService: DrivingSchoolConfigurationService
    ) {
    }

    updateEvent(): void {
        if (this.blocked && (this.event.meta.blocked !== this.blocked)) {
            this.event.meta.associated.blockedDates.push(this.event.start);
            this.calendarService.addBlockedTimeSlot(this.event.meta.associated, this.event.start);
        }

        if (!this.blocked && (this.event.meta.blocked !== this.blocked)) {
            this.event.meta.associated.blockedDates.push(this.event.start);
            this.calendarService.removeBlockedTimeSlot(this.event.meta.associated, this.event.start);
        }

        const prefCat = this.form.get('preferredCategories').value;
        const optCat = this.form.get('optionalCategories').value;

        this.event.meta.associated.preferredCategories = prefCat;
        this.event.meta.associated.optionalCategories = optCat;

        const slot = {
            id: this.event.meta.associated.id,
            preferredCategories: prefCat,
            optionalCategories: optCat
        };

        this.calendarService.updateTimeSlot(slot);

        this.eventManager.broadcast({
            name: 'refreshCalendarView'
        });

        this.activeModal.dismiss('update');
    }

    createTimeSlot(): void {
        const dayPipeRef = this.datepipe.transform(this.event.start, 'EEEEEE').toString().toUpperCase().substr(0, 2);
        const day = DayOfWeek[dayPipeRef];

        const dayRef = TimeSlotModalComponent.parseDayOfWeek(day) * 86400000 + this.weekRef;
        const start = (this.event.start.getTime() - dayRef) / 60000;
        const end = (this.event.end.getTime() - dayRef) / 60000;

        const prefCat = this.form.get('preferredCategories').value;
        const optCat = this.form.get('optionalCategories').value;

        const slot = {
            day: day,
            begin: start,
            end: end,
            preferredCategories: prefCat,
            optionalCategories: optCat,
            teacherId: this.teacher.id
        };

        this.calendarService.createTimeSlot(slot);

        this.event.meta.type = EventType.TIME_SLOT;

        this.activeModal.dismiss('create');
    }

    deleteTimeSlot(): void {
        this.calendarService.deleteTimeSlot(this.event.meta.associated);

        this.eventManager.broadcast({
            name: 'deleteTimeSlotEvent',
            content: this.event
        });

        this.activeModal.dismiss('cancel');
    }

    private static parseDayOfWeek(day: DayOfWeek): number {
        switch (day) {
            case DayOfWeek.MO:
                return 0;
            case DayOfWeek.TU:
                return 1;
            case DayOfWeek.WE:
                return 2;
            case DayOfWeek.TH:
                return 3;
            case DayOfWeek.FR:
                return 4;
            case DayOfWeek.SA:
                return 5;
            case DayOfWeek.SU:
                return 6;
        }
    }

    updateForm() {
        this.form.patchValue({
            preferredCategories: this.event.meta.associated.preferredCategories,
            optionalCategories: this.event.meta.associated.optionalCategories
        });
    }

    ngOnInit(): void {
		if (this.event.meta.type !== EventType.FRESH) {
            this._fresh = false;
            this.updateForm();
            this.blocked = this.event.meta.blocked;
        } else {
            this._fresh = true;
            this.calendarService.getTeacher().subscribe(teacher => this.teacher = teacher);
        }

        this.drivingSchoolConfigurationService.fetchAvailableDrivingCategories().subscribe(
            drivingCategories =>{
                this.drivingCategoryOptions = drivingCategories;
            }
        );
    }

    cancel() {
        if (this._fresh) {
            this.eventManager.broadcast({
                name: 'deleteTimeSlotEvent',
                content: this.event
            });
        }
        this.activeModal.dismiss('cancel');
    }
}
