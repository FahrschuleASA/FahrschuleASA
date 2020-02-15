import {Component, OnInit} from '@angular/core';

import {FormBuilder,Validators } from '@angular/forms';
import {MyAccount} from "../../shared/model/my-account.model";
import {MyAccountService} from "../../entities/my-account/my-account.service";

@Component({
    selector: 'jhi-settings',
    templateUrl: './personal-data.component.html',
    styleUrls: ['./personal-data.component.scss']
})
export class PersonalDataComponent implements OnInit {
    error: string;
    success: string;
    languages: any[];
    account: MyAccount;
    birthDate: string;

    personalDataForm = this.fb.group({
        email: [undefined, [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
        phoneNumber: [undefined, [Validators.required, Validators.pattern(/^[0-9]+$/), Validators.maxLength(20)]],
    });

    constructor(
        private myAccountService: MyAccountService,
        private fb: FormBuilder
    ) {
    }

    ngOnInit() {
        this.myAccountService.fetch().subscribe( account => {
            this.account = account;
            this.birthDate = this.myAccountService.transformDateForFrontend(this.account.birthdate);
            this.updateForm(account);
            }
        );
    }

    save() {
        const personalDataAccount = this.accountFromForm();
        this.myAccountService.updateStudent(personalDataAccount).subscribe(
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

    private accountFromForm(): MyAccount {
        if (this.personalDataForm.get('email').dirty){
            this.account.user.email = this.personalDataForm.get('email').value;
        }
        if (this.personalDataForm.get('phoneNumber').dirty){
            this.account.phoneNumber = this.personalDataForm.get('phoneNumber').value;
        }
        return this.account;
    }

    updateForm(account: MyAccount): void {
        this.personalDataForm.patchValue({
            email: account.newEmail ? account.newEmail : account.user.email,
            phoneNumber: account.phoneNumber
        });
    }
}
