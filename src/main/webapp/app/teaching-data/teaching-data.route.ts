import {Route} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {TeachingDataComponent} from './teaching-data.component';

export const teachingDataRoute: Route = {
    path: 'teaching-data',
    component: TeachingDataComponent,
    data: {
        authorities: ['ROLE_STUDENT'],
        pageTitle: 'fahrschuleAsaApp.teachingData.home.title'
    },
    canActivate: [UserRouteAccessService]
};
