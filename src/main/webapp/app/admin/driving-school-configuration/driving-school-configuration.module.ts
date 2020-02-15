import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FahrschuleAsaSharedModule} from 'app/shared/shared.module';

import {DrivingSchoolConfigurationComponent} from './driving-school-configuration.component';

import {drivingSchoolConfigurationRoute} from './driving-school-configuration.route';

@NgModule({
    imports: [FahrschuleAsaSharedModule, RouterModule.forChild([drivingSchoolConfigurationRoute])],
    declarations: [DrivingSchoolConfigurationComponent]
})
export class DrivingSchoolConfigurationModule {
}
