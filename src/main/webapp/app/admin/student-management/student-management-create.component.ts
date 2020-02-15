import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {HttpResponse} from '@angular/common/http';

import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {StudentService} from "../../entities/student/student.service";
import {DrivingSchoolConfigurationService} from "../driving-school-configuration/driving-school-configuration.service";
import {Teacher} from "../../shared/model/teacher.model";
import {TeacherService} from "../../entities/teacher/teacher.service";
import {JhiAlertService} from 'ng-jhipster';

@Component({
    selector: 'jhi-student-mgmt-create',
    templateUrl: './student-management-create.component.html'
})
export class StudentManagementCreateComponent implements OnInit {
    isSaving: boolean;
    availableCategories: string[];
    availableTeachers: Teacher[];

    editForm = this.fb.group({
        firstName: [undefined, [Validators.required, Validators.maxLength(50)]],
        lastName: [undefined, [Validators.required, Validators.maxLength(50)]],
        birthDate: [undefined, [Validators.required, Validators.pattern(/^[0-9][0-9]\.[0-9][0-9]\.[0-9][0-9][0-9][0-9]$/)]],
        phoneNumber: [undefined, [Validators.required, Validators.pattern(/^[0-9]+$/), Validators.maxLength(20)]],
        email: [undefined, [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
        street: [undefined, [Validators.required]],
        houseNumber: [undefined, [Validators.required, Validators.pattern(/^[0-9]*$/)]],
        postal: [undefined, [Validators.required, Validators.pattern(/^[0-9][0-9][0-9][0-9][0-9]$/)]],
        town: [undefined, [Validators.required]],
        country: [undefined, [Validators.required]],
        additional: [undefined],
        category: [null, [Validators.required]],
        teacher: [null, [Validators.required]]
    });

    constructor(
        private studentService: StudentService,
        private teacherService: TeacherService,
        private drivingSchoolConfigurationService: DrivingSchoolConfigurationService,
        private alertService: JhiAlertService,
        private languageHelper: JhiLanguageHelper,
        private route: ActivatedRoute,
        private fb: FormBuilder
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.drivingSchoolConfigurationService.fetchAvailableDrivingCategories().subscribe(
          schoolConfiguration =>{
              this.availableCategories = schoolConfiguration;
          }
        );
        this.teacherService.query().subscribe(
            (res: HttpResponse<Teacher[]>) => this.onTeachersSuccess(res.body),
            (res: HttpResponse<any>) => this.onError(res.body)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        const student = this.studentFromForm();
        this.studentService.create(student).subscribe(response => this.onSaveSuccess(response), () => this.onSaveError());
    }

    private studentFromForm(): any {
        const myStudent = {};
        return {
            ...myStudent,
            user: {
                firstName: this.editForm.get('firstName').value,
                lastName: this.editForm.get('lastName').value,
                email: this.editForm.get('email').value,
                authorities: ['ROLE_STUDENT', 'ROLE_USER']
            },
            birthdate: this.studentService.transformDateForDB(
                this.editForm.get('birthDate').value
            ),
            address: {
                town: this.editForm.get('town').value,
                street: this.editForm.get('street').value,
                postal: this.editForm.get('postal').value,
                houseNumber: this.editForm.get('houseNumber').value,
                country: this.editForm.get('country').value,
                additional: this.editForm.get('additional').value
            },
            phoneNumber: this.editForm.get('phoneNumber').value,
            teacherId: this.editForm.get('teacher').value,
            category: this.editForm.get('category').value
        };
    }

    private onSaveSuccess(result) {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onTeachersSuccess(data) {
        this.availableTeachers = data;
    }

    private onError(error) {
        this.alertService.error(error.error, error.message, null);
    }

    // helps to find invalid controls
    // (change)="findInvalidControls()"
    public findInvalidControls() {
        const invalid = [];
        const controls = this.editForm.controls;
        for (const name in controls) {
            if (controls[name].invalid) {
                invalid.push(name);
                console.log(name);
            }
        }
        return invalid;
    }
}
