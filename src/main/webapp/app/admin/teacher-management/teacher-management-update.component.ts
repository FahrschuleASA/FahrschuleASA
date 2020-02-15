import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {Teacher} from "../../shared/model/teacher.model";
import {TeacherService} from "../../entities/teacher/teacher.service";

@Component({
    selector: 'jhi-teacher-mgmt-update',
    templateUrl: './teacher-management-update.component.html',
    styleUrls: ['./teacher-management.scss']
})
export class TeacherManagementUpdateComponent implements OnInit {
    teacher: Teacher;
    isSaving: boolean;

    editForm = this.fb.group({
        firstName: [undefined, [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
        lastName: [undefined, [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
        email: [undefined, [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
        birthDate: [undefined, [Validators.required, Validators.pattern(/^[0-9][0-9]\.[0-9][0-9]\.[0-9][0-9][0-9][0-9]$/)]],
        street: [undefined, [Validators.required]],
        houseNumber: [undefined, [Validators.required, Validators.pattern(/^[0-9]*$/)]],
        postal: [undefined, [Validators.required, Validators.pattern(/^[0-9][0-9][0-9][0-9][0-9]$/)]],
        town: [undefined, [Validators.required]],
        country: [undefined, [Validators.required]],
        additional: [undefined],
        phoneNumber: [undefined, [Validators.required, Validators.pattern(/^[0-9]+$/), Validators.maxLength(20)]]
    });

    constructor(
        private languageHelper: JhiLanguageHelper,
        private teacherService: TeacherService,
        private route: ActivatedRoute,
        private fb: FormBuilder
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.route.data.subscribe(({teacher}) => {
            this.teacher = teacher.body ? teacher.body : teacher;
            this.updateForm(this.teacher);
        });
    }

    private updateForm(teacher: Teacher): void {
        this.editForm.patchValue({
            id: teacher.user.id,
            firstName: teacher.user.firstName,
            lastName: teacher.user.lastName,
            login: teacher.user.login,
            email: teacher.newEmail ? teacher.newEmail : teacher.user.email,
            phoneNumber: teacher.phoneNumber,
            birthDate: this.teacherService.transformDateForFrontend(
                teacher.birthdate
            ),
            street: teacher.address.street,
            houseNumber: teacher.address.houseNumber,
            postal: teacher.address.postal,
            town: teacher.address.town,
            country: teacher.address.country,
            additional: teacher.address.additional
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.updateTeacher(this.teacher);
        if (this.teacher.id !== null) {
            this.teacherService.update(this.teacher).subscribe(response => this.onSaveSuccess(response), () => this.onSaveError());
        }
    }

    private updateTeacher(teacher: Teacher): void {
        teacher.user.firstName = this.editForm.get(['firstName']).value;
        teacher.user.lastName = this.editForm.get(['lastName']).value;
        teacher.user.email = this.editForm.get(['email']).value;
        teacher.phoneNumber = this.editForm.get(['phoneNumber']).value;
        teacher.birthdate = this.teacherService.transformDateForDB(this.editForm.get(['birthDate']).value);
        teacher.address.town = this.editForm.get(['town']).value;
        teacher.address.street = this.editForm.get(['street']).value;
        teacher.address.postal = this.editForm.get(['postal']).value;
        teacher.address.houseNumber = this.editForm.get(['houseNumber']).value;
        teacher.address.country = this.editForm.get(['country']).value;
        teacher.address.additional = this.editForm.get(['additional']).value;
    }

    private onSaveSuccess(result) {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }


}
