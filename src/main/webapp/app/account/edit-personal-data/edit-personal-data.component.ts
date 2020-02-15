import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';

import {MyAccountService} from "../../entities/my-account/my-account.service";
import {MyAccount} from "../../shared/model/my-account.model";

@Component({
    selector: 'jhi-settings',
    templateUrl: './edit-personal-data.component.html',
    styleUrls: ['./edit-personal-data.component.scss']
})
export class EditPersonalDataComponent implements OnInit {
    error: string;
    success: string;
    languages: any[];
    account: MyAccount;

    personalDataForm = this.fb.group({
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
        login: []
    });

    constructor(
        private myAccountService: MyAccountService,
        private fb: FormBuilder
    ) {
    }

    ngOnInit() {
        this.myAccountService.fetch().subscribe( account => {
            this.updateForm(account);
            this.account = account;
        });
    }

    save() {
        this.accountFromForm();
        this.myAccountService.update(this.account).subscribe(
            () => {
                this.error = null;
                this.success = 'OK';
                this.myAccountService.fetch().subscribe(account => {
                    this.updateForm(account);
                });
            },
            () => {
                this.success = null;
                this.error = 'ERROR';
            }
        );
    }

    private accountFromForm(): void {
        this.account.user.firstName = this.personalDataForm.get('firstName').value;
        this.account.user.lastName = this.personalDataForm.get('lastName').value;
        this.account.user.email = this.personalDataForm.get('email').value;
        this.account.birthdate = this.myAccountService.transformDateForDB(
            this.personalDataForm.get('birthDate').value
        );
        this.account.address.town = this.personalDataForm.get('town').value;
        this.account.address.street = this.personalDataForm.get('street').value;
        this.account.address.postal = this.personalDataForm.get('postal').value;
        this.account.address.houseNumber = this.personalDataForm.get('houseNumber').value;
        this.account.address.country = this.personalDataForm.get('country').value;
        this.account.address.additional = this.personalDataForm.get('additional').value;
        this.account.phoneNumber = this.personalDataForm.get('phoneNumber').value;
    }

    updateForm(account: MyAccount): void {
        this.personalDataForm.patchValue({
            firstName: account.user.firstName,
            lastName: account.user.lastName,
            login: account.user.login,
            email: account.user.email,
            phoneNumber: account.phoneNumber,
            birthDate: this.myAccountService.transformDateForFrontend(
                account.birthdate
            ),
            street: account.address.street,
            houseNumber: account.address.houseNumber,
            postal: account.address.postal,
            town: account.address.town,
            country: account.address.country,
            additional: account.address.additional
        });
    }
}
