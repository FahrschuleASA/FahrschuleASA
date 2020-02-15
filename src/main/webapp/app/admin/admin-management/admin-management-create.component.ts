import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {DrivingSchoolConfigurationService} from "../driving-school-configuration/driving-school-configuration.service";
import {AdminService} from "./admin.service";

@Component({
    selector: 'jhi-admin-mgmt-create',
    templateUrl: './admin-management-create.component.html',
    styleUrls: ['./admin-management.scss']
})
export class AdminManagementCreateComponent implements OnInit {
    isSaving: boolean;
    availableCategories: string[];

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
        phoneNumber: [undefined, [Validators.required, Validators.pattern(/^[0-9]+$/), Validators.maxLength(20)]],
        schoolOwner: [undefined]
    });

    constructor(
        private languageHelper: JhiLanguageHelper,
        private adminService: AdminService,
        private drivingSchoolConfigurationService: DrivingSchoolConfigurationService,
        private route: ActivatedRoute,
        private fb: FormBuilder
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.drivingSchoolConfigurationService.fetch().subscribe(
            schoolConfiguration =>{
                this.availableCategories = schoolConfiguration.availableCategories;
            }
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        const admin = this.adminFromForm();
        this.adminService.create(admin).subscribe(response => this.onSaveSuccess(response), () => this.onSaveError());
    }

    private adminFromForm(): any {
        const admin = {};
        return {
            ...admin,
            user: {
                authorities: ['ROLE_USER', 'ROLE_ADMIN'],
                firstName: this.editForm.get(['firstName']).value,
                lastName: this.editForm.get(['lastName']).value,
                email: this.editForm.get(['email']).value
            },
            phoneNumber: this.editForm.get(['phoneNumber']).value,
            birthdate: this.adminService.transformDateForDB(this.editForm.get(['birthDate']).value),
            address: {
                town: this.editForm.get(['town']).value,
                street: this.editForm.get(['street']).value,
                postal: this.editForm.get(['postal']).value,
                houseNumber: this.editForm.get(['houseNumber']).value,
                country: this.editForm.get(['country']).value,
                additional: this.editForm.get(['additional']).value
            },
            schoolOwner: this.editForm.get(['schoolOwner']).value
        }
    }

    private onSaveSuccess(result) {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
