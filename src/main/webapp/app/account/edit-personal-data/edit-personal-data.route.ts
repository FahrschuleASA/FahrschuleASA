import {Route} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {EditPersonalDataComponent} from './edit-personal-data.component';

export const editPersonalDataRoute: Route = {
    path: 'edit-personal-data',
    component: EditPersonalDataComponent,
    data: {
        authorities: ['ROLE_ADMIN', 'ROLE_TEACHER'],
        pageTitle: 'global.menu.account.editPersonalData'
    },
    canActivate: [UserRouteAccessService]
};
