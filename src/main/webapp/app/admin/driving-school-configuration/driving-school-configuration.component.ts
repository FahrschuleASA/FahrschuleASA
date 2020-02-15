import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {Configuration, Weekday} from "../../shared/model/configuration.model";

import {DrivingSchoolConfigurationService} from './driving-school-configuration.service';

@Component({
    selector: 'jhi-driving-school-configuration',
    templateUrl: './driving-school-configuration.component.html',
    styleUrls: ['./driving-school-configuration.scss']
})
export class DrivingSchoolConfigurationComponent implements OnInit {
    error: string;
    success: string;
    drivingSchoolConfigurationForm = this.fb.group({
        initialLessons:     [undefined, [Validators.required]],
        planningWeekday:    [undefined, [Validators.required]],
        planningHour:       [undefined, [Validators.required]],
        planningMinute:     [undefined, [Validators.required]],
        maxInactive:        [undefined, [Validators.required]],
        deadlineMissedLesson:   [undefined, [Validators.required]],
        availableCategories:    [undefined, [Validators.required]],
        emailSignature: [undefined, [Validators.required]]
    });
    planningHourOptions;
    planningMinuteOptions;
    allCategories;

    constructor(
        private drivingSchoolConfigurationService: DrivingSchoolConfigurationService,
        private fb: FormBuilder
    ) {
        this.planningHourOptions = Array(24).fill(0).map((x,i)=>i);
        this.planningMinuteOptions = Array(60).fill(0).map((x,i)=>i);
    }

    ngOnInit() {
        this.drivingSchoolConfigurationService.fetch().subscribe( configuration => {
           this.updateForm(configuration);
           this.allCategories = configuration.allCategories;
        });
    }

    save() {
        const configuration = this.configurationFromForm();
        this.drivingSchoolConfigurationService.save(configuration).subscribe(
            () => {
                this.error = null;
                this.success = 'OK';
                this.drivingSchoolConfigurationService.fetch().subscribe(configuration => {
                    this.updateForm(configuration);
                });
            },
            () => {
                this.success = null;
                this.error = 'ERROR';
            }
        );
    }

    private configurationFromForm(): any {
        const configuration = {};
        return {
            ...configuration,
            initialLessons: this.drivingSchoolConfigurationForm.get('initialLessons').value,
            planningWeekday: this.drivingSchoolConfigurationForm.get('planningWeekday').value,
            planningHour: this.drivingSchoolConfigurationForm.get('planningHour').value,
            planningMinute: this.drivingSchoolConfigurationForm.get('planningMinute').value,
            maxInactive: this.drivingSchoolConfigurationForm.get('maxInactive').value,
            deadlineMissedLesson: this.drivingSchoolConfigurationForm.get('deadlineMissedLesson').value,
            availableCategories: this.drivingSchoolConfigurationForm.get('availableCategories').value,
            emailSignature: this.drivingSchoolConfigurationForm.get('emailSignature').value
        };
    }

    updateForm(configuration: Configuration): void {
        this.drivingSchoolConfigurationForm.patchValue({
            initialLessons: configuration.initialLessons,
            planningWeekday: configuration.planningWeekday,
            planningHour: configuration.planningHour,
            planningMinute: configuration.planningMinute,
            maxInactive: configuration.maxInactive,
            deadlineMissedLesson: configuration.deadlineMissedLesson,
            availableCategories: configuration.availableCategories,
            emailSignature: configuration.emailSignature
        });
    }


}
