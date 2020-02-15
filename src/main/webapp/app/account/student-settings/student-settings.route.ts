import {Route} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {StudentSettingsComponent} from './student-settings.component';

export const studentSettingsRoute: Route = {
    path: 'student-settings',
    component: StudentSettingsComponent,
    data: {
        authorities: ['ROLE_STUDENT'],
        pageTitle: 'global.menu.account.settings'
    },
    canActivate: [UserRouteAccessService]
};
