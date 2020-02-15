import {Route} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {PersonalDataComponent} from './personal-data.component';

export const personalDataRoute: Route = {
    path: 'personal-data',
    component: PersonalDataComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'global.menu.account.personalData'
    },
    canActivate: [UserRouteAccessService]
};
