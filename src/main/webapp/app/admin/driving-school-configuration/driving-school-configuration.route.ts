import {Route} from '@angular/router';

import {DrivingSchoolConfigurationComponent} from './driving-school-configuration.component';

export const drivingSchoolConfigurationRoute: Route = {
    path: '',
    component: DrivingSchoolConfigurationComponent,
    data: {
        pageTitle: 'drivingSchoolConfiguration.title'
    }
};
