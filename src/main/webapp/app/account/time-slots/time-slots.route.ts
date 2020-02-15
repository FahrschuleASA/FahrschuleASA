import {Route} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {TimeSlotsComponent} from "app/account/time-slots/time-slots.component";

export const timeSlotsRoute: Route = {
    path: 'time-slots',
    component: TimeSlotsComponent,
    data: {
        authorities: ['ROLE_TEACHER'],
        pageTitle: 'fahrschuleAsaApp.teachingData.home.title'
    },
    canActivate: [UserRouteAccessService]
};
