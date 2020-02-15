import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {AdminService} from "./admin.service";
import {MyAccount} from "../../shared/model/my-account.model";

@Component({
    selector: 'jhi-admin-mgmt-update',
    templateUrl: './admin-management-update.component.html',
    styleUrls: ['./admin-management.scss']
})
export class AdminManagementUpdateComponent implements OnInit {
    admin: MyAccount;
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
        private adminService: AdminService,
        private route: ActivatedRoute,
        private fb: FormBuilder
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.route.data.subscribe(({admin}) => {
            this.admin = admin.body ? admin.body : admin;
            this.updateForm(this.admin);
        });
    }

    private updateForm(admin: MyAccount): void {
        this.editForm.patchValue({
            id: admin.user.id,
            firstName: admin.user.firstName,
            lastName: admin.user.lastName,
            login: admin.user.login,
            email: admin.newEmail ? admin.newEmail : admin.user.email,
            phoneNumber: admin.phoneNumber,
            birthDate: this.adminService.transformDateForFrontend(
                admin.birthdate
            ),
            street: admin.address.street,
            houseNumber: admin.address.houseNumber,
            postal: admin.address.postal,
            town: admin.address.town,
            country: admin.address.country,
            additional: admin.address.additional
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.updateAdmin(this.admin);
        if (this.admin.id !== null) {
            this.adminService.update(this.admin).subscribe(response => this.onSaveSuccess(response), () => this.onSaveError());
        }
    }

    private updateAdmin(admin: MyAccount): void {
        admin.user.firstName = this.editForm.get(['firstName']).value;
        admin.user.lastName = this.editForm.get(['lastName']).value;
        admin.user.email = this.editForm.get(['email']).value;
        admin.phoneNumber = this.editForm.get(['phoneNumber']).value;
        admin.birthdate = this.adminService.transformDateForDB(this.editForm.get(['birthDate']).value);
        admin.address.town = this.editForm.get(['town']).value;
        admin.address.street = this.editForm.get(['street']).value;
        admin.address.postal = this.editForm.get(['postal']).value;
        admin.address.houseNumber = this.editForm.get(['houseNumber']).value;
        admin.address.country = this.editForm.get(['country']).value;
        admin.address.additional = this.editForm.get(['additional']).value;
    }

    private onSaveSuccess(result) {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }


}
