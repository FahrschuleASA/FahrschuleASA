import {AfterViewInit, Component, ElementRef, Renderer, Input, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';
import {Router} from '@angular/router';
import {CalendarService} from "app/home/calendar-utils/calendar.service";
import {Student} from "app/shared/model/student.model";
import {Location} from "app/shared/model/location.model";

@Component({
    selector: 'location-modal',
    templateUrl: './location.modal.component.html',
    providers: [CalendarService]
})
export class LocationModalComponent implements OnInit {

    @Input() designatedStudentId;

    eventForm = this.fb.group({
        street: [undefined, [Validators.required]],
        housenumber: [undefined, [Validators.required, Validators.pattern(/^[0-9]*$/)]],
        zipcode: [undefined, [Validators.required, Validators.pattern(/^[0-9][0-9][0-9][0-9][0-9]$/)]],
        city: [undefined, [Validators.required]],
        country: [undefined, [Validators.required]],
        additional: [undefined]
    });

    student: Student = null;

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
    }

    ngOnInit(): void {
        this.getStudent();
    }

    cancel() {
        this.activeModal.dismiss('cancel');
    }

    updateForm() : void {
        this.eventForm.patchValue({
            street: this.student.address.street,
            housenumber: this.student.address.houseNumber,
            zipcode: this.student.address.postal,
            city: this.student.address.town,
            country: this.student.address.country,
            additional: this.student.address.additional
        });
    }

    getStudent() {
        if (this.student == null) {
            if (this.designatedStudentId) {
                this.calendarService.getStudent(this.designatedStudentId).subscribe(val => {
                    this.student = val;
                    this.updateForm();
                }, error => {
                    this.student = new Student();
                });
            } else {
                this.calendarService.getStudent().subscribe(student => {
                    this.student = student
                    this.updateForm();
                }, error => {
                    this.student = new Student();
                });
            }
        }
        return this.student;
    }

    emitLocation(): void {
        const city = this.eventForm.get('city').value !== '' ? this.eventForm.get('city').value : this.getStudent().address.town;
        const street = this.eventForm.get('street').value !== '' ? this.eventForm.get('street').value : this.getStudent().address.street;
        const zipcode = this.eventForm.get('zipcode').value !== '' ? this.eventForm.get('zipcode').value : this.getStudent().address.postal;
        const housenumber = this.eventForm.get('housenumber').value !== '' ? this.eventForm.get('housenumber').value : this.getStudent().address.houseNumber;
        const country = this.eventForm.get('country').value !== '' ? this.eventForm.get('country').value : this.getStudent().address.town;
        const additional = this.eventForm.get('additional').value !== '' ? this.eventForm.get('additional').value : this.getStudent().address.additional;

        const loc = new Location();
        loc.additional = additional;
        loc.country = country;
        loc.town = city;
        loc.postal = zipcode;
        loc.houseNumber = housenumber;
        loc.street = street;

        this.activeModal.close(loc);
    }
}
