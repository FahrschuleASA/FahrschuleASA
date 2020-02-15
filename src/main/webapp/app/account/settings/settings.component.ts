import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {JhiLanguageService} from 'ng-jhipster';

import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {MyAccountService} from "../../entities/my-account/my-account.service";
import {MyAccount} from "../../shared/model/my-account.model";
import {DrivingSchoolConfigurationService} from "../../admin/driving-school-configuration/driving-school-configuration.service";
import {AccountService} from "../../core/auth/account.service";

@Component({
    selector: 'jhi-settings',
    templateUrl: './settings.component.html',
    styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
    error: string;
    success: string;
    languages: any[];
    account: MyAccount;

    maxInactive: number;
    minInactive: number;

    settingsForm = this.fb.group({
        active: [undefined],
        inactiveDuration: [1, [Validators.min(this.minInactive), Validators.max(this.maxInactive)]],
        langKey: ['de']
    });

    constructor(
        private myAccountService: MyAccountService,
        private accountService: AccountService,
        private drivingSchoolConfigurationService: DrivingSchoolConfigurationService,
        private fb: FormBuilder,
        private languageService: JhiLanguageService,
        private languageHelper: JhiLanguageHelper
    ) {
    }

    ngOnInit() {
        this.myAccountService.fetch().subscribe(account => {
            this.updateForm(account);
            this.account = account;
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
        this.accountFromForm();
        this.myAccountService.updateTeacher(this.account).subscribe(
            () => {
                this.error = null;
                this.success = 'OK';
                this.myAccountService.fetch().subscribe(account => {
                    this.updateForm(account);
                });
                this.languageService.getCurrent().then(current => {
                    if (this.account.user.langKey !== current) {
                        this.languageService.changeLanguage(this.account.user.langKey);
                    }
                });
            },
            () => {
                this.success = null;
                this.error = 'ERROR';
            }
        );
    }

    private accountFromForm(): void {
        this.account.active = this.settingsForm.get('active').value;
        this.account.deactivatedDaysLeft = this.settingsForm.get('active').value ? null : this.settingsForm.get('inactiveDuration').value;
        this.account.user.langKey = this.settingsForm.get('langKey').value;
    }

    updateForm(account: MyAccount): void {
        this.settingsForm.patchValue({
            active: account.active,
            inactiveDuration: account.deactivatedDaysLeft ? account.deactivatedDaysLeft : Number(account.active),
            langKey: account.user.langKey
        });
    }
}
