import {Route} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {PreferencesComponent} from "app/account/preferences/preferences.component";

export const preferencesRoute: Route = {
    path: 'preferences',
    component: PreferencesComponent,
    data: {
        authorities: ['ROLE_STUDENT'],
        pageTitle: 'fahrschuleAsaApp.teachingData.home.title'
    },
    canActivate: [UserRouteAccessService]
};
