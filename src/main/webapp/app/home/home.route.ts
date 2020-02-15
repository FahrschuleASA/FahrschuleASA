import {Routes} from '@angular/router';

import {HomeComponent} from './home.component';
import {UserRouteAccessService} from "app/core/auth/user-route-access-service";
import {LoginscreenComponent} from "app/loginscreen/loginscreen.component";

export const HOME_ROUTES: Routes = [
    {
        path: '',
        component: HomeComponent,
        data: {
            authorities: ["ROLE_USER"],
            pageTitle: 'home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: "login",
        component: LoginscreenComponent,
        data: {
            pageTitle: "home.title"
        }
    }
];
