import {AfterViewInit, Component, ElementRef, Renderer, Input, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';
import {Router} from '@angular/router';
import {CalendarService} from "app/home/calendar-utils/calendar.service";
import {Student} from "app/shared/model/student.model";
import {Location} from "app/shared/model/location.model";

@Component({
    selector: 'double-location-modal',
    templateUrl: './double-location.modal.component.html',
    providers: [CalendarService]
})
export class DoubleLocationModalComponent implements OnInit {

    @Input() designatedStudentId;

    pickupEventForm = this.fb.group({
        pickupstreet: '',
        pickuphousenumber: '',
        pickupzipcode: '',
        pickupcity: '',
        pickupcountry: '',
        pickupadditional: ''
    });

    destEventForm = this.fb.group({
        deststreet: '',
        desthousenumber: '',
        destzipcode: '',
        destcity: '',
        destcountry: '',
        destadditional: ''
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


    getStudent() {
        if (this.student == null) {
            if (this.designatedStudentId) {
                this.calendarService.getStudent(this.designatedStudentId).subscribe(val => {
                    this.student = val;
                }, error => {
                    this.student = new Student();
                });
            } else {
                this.calendarService.getStudent().subscribe(student => {
                    this.student = student
                }, error => {
                    this.student = new Student();
                });
            }
        }

        return this.student;
    }

    emitLocation(): void {
        const pickupCity = this.pickupEventForm.get('pickupcity').value !== '' ? this.pickupEventForm.get('pickupcity').value : this.getStudent().address.town;
        const pickupStreet = this.pickupEventForm.get('pickupstreet').value !== '' ? this.pickupEventForm.get('pickupstreet').value : this.getStudent().address.street;
        const pickupZipcode = this.pickupEventForm.get('pickupzipcode').value !== '' ? this.pickupEventForm.get('pickupzipcode').value : this.getStudent().address.postal;
        const pickupHousenumber = this.pickupEventForm.get('pickuphousenumber').value !== '' ? this.pickupEventForm.get('pickuphousenumber').value : this.getStudent().address.houseNumber;
        const pickupCountry = this.pickupEventForm.get('pickupcountry').value !== '' ? this.pickupEventForm.get('pickupcountry').value : this.getStudent().address.town;
        const pickupAdditional = this.pickupEventForm.get('pickupadditional').value !== '' ? this.pickupEventForm.get('pickupadditional').value : this.getStudent().address.additional;

        const destCity = this.destEventForm.get('destcity').value !== '' ? this.destEventForm.get('destcity').value : this.getStudent().address.town;
        const destStreet = this.destEventForm.get('deststreet').value !== '' ? this.destEventForm.get('deststreet').value : this.getStudent().address.street;
        const destZipcode = this.destEventForm.get('destzipcode').value !== '' ? this.destEventForm.get('destzipcode').value : this.getStudent().address.postal;
        const destHousenumber = this.destEventForm.get('desthousenumber').value !== '' ? this.destEventForm.get('desthousenumber').value : this.getStudent().address.houseNumber;
        const destCountry = this.destEventForm.get('destcountry').value !== '' ? this.destEventForm.get('destcountry').value : this.getStudent().address.town;
        const destAdditional = this.destEventForm.get('destadditional').value !== '' ? this.destEventForm.get('destadditional').value : this.getStudent().address.additional;

        const pickupLoc = new Location();
        pickupLoc.additional = pickupAdditional;
        pickupLoc.country = pickupCountry;
        pickupLoc.town = pickupCity;
        pickupLoc.postal = pickupZipcode;
        pickupLoc.houseNumber = pickupHousenumber;
        pickupLoc.street = pickupStreet;

        const destLoc = new Location();
        destLoc.additional = destAdditional;
        destLoc.country = destCountry;
        destLoc.town = destCity;
        destLoc.postal = destZipcode;
        destLoc.houseNumber = destHousenumber;
        destLoc.street = destStreet;

        this.activeModal.close({pickup: pickupLoc, destination: destLoc});
    }
}
