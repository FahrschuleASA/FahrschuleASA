import {Component, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {Router} from '@angular/router';
import {JhiEventManager} from 'ng-jhipster';

import {StateStorageService} from "../core/auth/state-storage.service";
import {LoginService} from "../core/login/login.service";


@Component({
    selector: 'jhi-loginscreen',
    templateUrl: './loginscreen.component.html',
    styleUrls: ['./loginscreen.component.scss']
})
export class LoginscreenComponent implements OnInit {
    authenticationError: boolean;

    loginForm = this.fb.group({
        username: [''],
        password: [''],
        rememberMe: [false]
    });

    constructor(
        private eventManager: JhiEventManager,
        private loginService: LoginService,
        private stateStorageService: StateStorageService,
        private router: Router,
        private fb: FormBuilder
    ) {
    }

    ngOnInit() {
    }

    cancel() {
        this.authenticationError = false;
        this.loginForm.patchValue({
            username: '',
            password: ''
        });
    }

    login() {
        this.loginForm.disable();
        this.loginService
            .login({
                username: this.loginForm.get('username').value,
                password: this.loginForm.get('password').value,
                rememberMe: this.loginForm.get('rememberMe').value
            })
            .subscribe(
                () => {
                    this.authenticationError = false;
                    if (
                        this.router.url === '/account/register' ||
                        this.router.url.startsWith('/account/activate') ||
                        this.router.url.startsWith('/account/reset/')
                    ) {
                        this.router.navigate(['']);
                    }

                    this.eventManager.broadcast({
                        name: 'authenticationSuccess',
                        content: 'Sending Authentication Success'
                    });

                    // previousState was set in the authExpiredInterceptor before being redirected to login modal.
                    // since login is successful, go to stored previousState and clear previousState
                    const redirect = this.stateStorageService.getUrl();
                    if (redirect) {
                        this.stateStorageService.storeUrl(null);
                        this.router.navigateByUrl(redirect);
                    }
                    // if no previousState is set, redirect to home.
                    else {
                        this.router.navigateByUrl('/');
                    }
                },
                () => {
                    this.authenticationError = true;
                    this.loginForm.enable();
                }
            );
    }

    requestResetPassword() {
        this.router.navigate(['/account/reset', 'request']);
    }

    loginFormDisabled(): boolean {
        return this.loginForm.disabled;
    }

}
