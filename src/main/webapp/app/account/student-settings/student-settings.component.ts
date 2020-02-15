import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {JhiLanguageService} from 'ng-jhipster';

import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {StudentService} from "../../entities/student/student.service";
import {Student} from "../../shared/model/student.model";
import {DrivingSchoolConfigurationService} from "../../admin/driving-school-configuration/driving-school-configuration.service";

@Component({
    selector: 'jhi-student-settings',
    templateUrl: './student-settings.component.html',
    styleUrls: ['./student-settings.component.scss']
})
export class StudentSettingsComponent implements OnInit {
    error: string;
    success: string;
    languages: any[];
    login: string;
    allowedLessons: number;

    maxInactive: number;
    minInactive: number;

    settingsForm = this.fb.group({
        active: [undefined, [Validators.required]],
        inactiveDuration: [1, [Validators.min(this.minInactive), Validators.max(this.maxInactive)]],
        langKey: [undefined, [Validators.required]],
        wantedLessons: [undefined, [Validators.required, Validators.min(1), Validators.max(20)]],
        readyForTheory: [undefined, [Validators.required]],
        notificationsBookableDrivingLesson: [undefined]
    });

    constructor(
        private studentService: StudentService,
        private drivingSchoolConfigurationService: DrivingSchoolConfigurationService,
        private fb: FormBuilder,
        private languageService: JhiLanguageService,
        private languageHelper: JhiLanguageHelper
    ) {
    }

    ngOnInit() {
        this.studentService.fetch().subscribe(student => {
            this.updateForm(student);
            this.login = student.user.login;
            this.allowedLessons = student.allowedLessons;
        });
        this.drivingSchoolConfigurationService.getMaxInactive().subscribe(
            maxInactive => {
                if (maxInactive == 0){
                    this.maxInactive = 3000;
                    this.minInactive = 0;
                }else{
                    this.maxInactive = maxInactive;
                    this.minInactive = 1;
                }
            });
        this.languages = this.languageHelper.getAll();

    }

    save() {
        const settingsStudent = this.studentFromForm();
        this.studentService.updateSettings(settingsStudent).subscribe(
            () => {
                this.error = null;
                this.success = 'OK';
                this.studentService.fetch().subscribe(student => {
                    this.updateForm(student);
                });
            },
            () => {
                this.success = null;
                this.error = 'ERROR';
            }
        );
    }

    private studentFromForm(): any {
        const student = {};
        return {
            ...student,
            active: this.settingsForm.get('active').value,
            deactivatedDaysLeft: this.settingsForm.get('active').value ? null : this.settingsForm.get('inactiveDuration').value,
            langKey: this.settingsForm.get('langKey').value,
            wantedLessons: this.settingsForm.get('wantedLessons').value,
            readyForTheory: this.settingsForm.get('readyForTheory').value,
            notifyForFreeLesson: this.settingsForm.get('notificationsBookableDrivingLesson').value
        };
    }

    updateForm(student: Student): void {
        this.settingsForm.patchValue({
            active: student.active,
            inactiveDuration: student.deactivatedDaysLeft ? student.deactivatedDaysLeft : Number(student.active),
            langKey: student.user.langKey,
            wantedLessons: student.wantedLessons,
            readyForTheory: student.readyForTheory,
            notificationsBookableDrivingLesson: student.notifyForFreeLesson
        });
    }
}
