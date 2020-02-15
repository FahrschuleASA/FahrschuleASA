import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {HttpResponse} from '@angular/common/http';

import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {Student} from "../../shared/model/student.model";
import {StudentService} from "../../entities/student/student.service";
import {Teacher} from "../../shared/model/teacher.model";
import {TeacherService} from "../../entities/teacher/teacher.service";
import {DrivingSchoolConfigurationService} from "../driving-school-configuration/driving-school-configuration.service";
import {JhiAlertService} from 'ng-jhipster';
import {TeachingDiagramService} from "../../entities/teaching-diagram/teaching-diagram.service";
import {TeachingDiagram} from "../../shared/model/teaching-diagram.model";

@Component({
    selector: 'jhi-student-mgmt-update',
    templateUrl: './student-management-update.component.html',
    styleUrls: ['student-management.scss']
})
export class StudentManagementUpdateComponent implements OnInit {
    personalDataShow = false;
    teachingDataShow = false;
    teachingDiagramShow = false;

    teachingDiagram: TeachingDiagram;

    student: Student;
    isSaving: boolean;
    availableCategories: string[];
    availableTeachers: Teacher[];

    editForm = this.fb.group({
        //personal data form
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
        //teaching data form
        category: [null, [Validators.required]],
        teacher: [null, [Validators.required]],
        allowedLessons: [2, [Validators.required]],
        drivingLicense: [false],
        //teaching diagram form
        performance: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
        independence: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
        advanced: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
        basic: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
        overland: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
        autobahn: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
        night: [0, [Validators.required, Validators.min(0), Validators.max(100)]]
    });

    constructor(
        private teachingDiagramService: TeachingDiagramService,
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
        this.route.data.subscribe(({student}) => {
            this.student = student.body ? student.body : student;
            this.updateFormPersonalData(this.student);
        });
        this.drivingSchoolConfigurationService.fetchAvailableDrivingCategories().subscribe(
            schoolConfiguration =>{
                this.availableCategories = schoolConfiguration;
            }
        );
        this.teacherService.query().subscribe(
            (res: HttpResponse<Teacher[]>) => this.onTeachersSuccess(res.body),
            (res: HttpResponse<any>) => this.onError(res.body)
        );
        this.teachingDiagramService.findEducationData(this.student.id).subscribe( teachingDiagram => {
            this.teachingDiagram = teachingDiagram;
            this.updateFormTeachingData(teachingDiagram);
        });
    }

    private updateFormPersonalData(student: Student): void {
        this.editForm.patchValue({
            id: student.id,
            login: student.user.login,
            firstName: student.user.firstName,
            lastName: student.user.lastName,
            email: student.newEmail ? student.newEmail : student.user.email,
            phoneNumber: student.phoneNumber,
            birthDate: this.studentService.transformDateForFrontend(
                student.birthdate
            ),
            street: student.address.street,
            houseNumber: student.address.houseNumber,
            postal: student.address.postal,
            town: student.address.town,
            country: student.address.country,
            additional: student.address.additional,
            category: student.category,
            teacher: student.teacherId
        });
    }

    private updateFormTeachingData(teachingData: TeachingDiagram): void {
        this.editForm.patchValue({
            category: teachingData.drivingCategory,
            teacher: teachingData.teacherId,
            allowedLessons: teachingData.allowedLessons,
            drivingLicense: teachingData.missionAccomplished,
            performance: teachingData.performance,
            independence: teachingData.independence,
            advanced: teachingData.advanced,
            basic: teachingData.basic,
            overland: teachingData.overland,
            autobahn: teachingData.autobahn,
            night: teachingData.night
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.updateUser();
        this.teachingDiagramService.saveEducationData(this.teachingDiagram).subscribe(response => {}, () => {});
        this.studentService.update(this.student).subscribe(response => this.onSaveSuccess(response), () => this.onSaveError());
    }

    private updateUser(): void {
        this.student.user.firstName = this.editForm.get(['firstName']).value;
        this.student.user.lastName = this.editForm.get(['lastName']).value;
        this.student.user.email = this.editForm.get(['email']).value;
        this.student.phoneNumber = this.editForm.get(['phoneNumber']).value;
        this.student.birthdate = this.studentService.transformDateForDB(this.editForm.get(['birthDate']).value);
        this.student.address.town = this.editForm.get(['town']).value;
        this.student.address.street = this.editForm.get(['street']).value;
        this.student.address.postal = this.editForm.get(['postal']).value;
        this.student.address.houseNumber = this.editForm.get(['houseNumber']).value;
        this.student.address.country = this.editForm.get(['country']).value;
        this.student.address.additional = this.editForm.get(['additional']).value;
        this.teachingDiagram.drivingCategory = this.editForm.get(['category']).value;
        this.teachingDiagram.teacherId = this.editForm.get(['teacher']).value;
        this.teachingDiagram.allowedLessons = this.editForm.get(['allowedLessons']).value;
        this.teachingDiagram.missionAccomplished = !!this.editForm.get(['drivingLicense']).value;
        this.teachingDiagram.performance = this.editForm.get(['performance']).value;
        this.teachingDiagram.independence = this.editForm.get(['independence']).value;
        this.teachingDiagram.advanced = this.editForm.get(['advanced']).value;
        this.teachingDiagram.basic = this.editForm.get(['basic']).value;
        this.teachingDiagram.overland = this.editForm.get(['overland']).value;
        this.teachingDiagram.autobahn = this.editForm.get(['autobahn']).value;
        this.teachingDiagram.night = this.editForm.get(['night']).value;
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

    togglePersonalData() {
        this.personalDataShow = !this.personalDataShow;
    }

    toggleTeachingData() {
        this.teachingDataShow = !this.teachingDataShow;
    }

    toggleTeachingDiagram() {
        this.teachingDiagramShow = !this.teachingDiagramShow;
    }
}
