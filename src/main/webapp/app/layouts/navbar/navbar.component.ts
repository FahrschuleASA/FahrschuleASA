import {Component, OnInit, AfterViewChecked, Self, ElementRef} from '@angular/core';
import {Router} from '@angular/router';
import {JhiLanguageService} from 'ng-jhipster';
import {SessionStorageService} from 'ngx-webstorage';

import {VERSION} from 'app/app.constants';
import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {AccountService} from 'app/core/auth/account.service';
import {LoginService} from 'app/core/login/login.service';
import {ProfileService} from 'app/layouts/profiles/profile.service';
import {NavbarService} from "app/layouts/navbar/navbar.service";

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['navbar.scss']
})
export class NavbarComponent implements OnInit, AfterViewChecked {
    inProduction: boolean;
    isNavbarCollapsed: boolean;
    languages: any[];
    swaggerEnabled: boolean;
    version: string;

    constructor(
        private loginService: LoginService,
        private languageService: JhiLanguageService,
        private languageHelper: JhiLanguageHelper,
        private sessionStorage: SessionStorageService,
        private accountService: AccountService,
        private profileService: ProfileService,
        private router: Router,
        private navbarService: NavbarService,
        @Self() private elementRef: ElementRef
    ) {
        this.version = VERSION ? (VERSION.toLowerCase().startsWith('v') ? VERSION : 'v' + VERSION) : '';
        this.isNavbarCollapsed = true;
    }

    ngOnInit() {
        this.languages = this.languageHelper.getAll();

        this.profileService.getProfileInfo().subscribe(profileInfo => {
            this.inProduction = profileInfo.inProduction;
            this.swaggerEnabled = profileInfo.swaggerEnabled;
        });
    }

    ngAfterViewChecked(): void {
        this.navbarService.setNavbarHeight(this.elementRef.nativeElement.offsetHeight);
    }

    changeLanguage(languageKey: string) {
        this.sessionStorage.store('locale', languageKey);
        this.languageService.changeLanguage(languageKey);
    }

    collapseNavbar() {
        this.isNavbarCollapsed = true;
    }

    isAuthenticated() {
        return this.accountService.isAuthenticated();
    }

    logout() {
        this.collapseNavbar();
        this.loginService.logout();
        this.router.navigate(['login']);
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

    getImageUrl() {
        return this.isAuthenticated() ? this.accountService.getImageUrl() : null;
    }
}
