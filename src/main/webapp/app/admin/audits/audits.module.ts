import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FahrschuleAsaSharedModule} from 'app/shared/shared.module';

import {AuditsComponent} from './audits.component';

import {auditsRoute} from './audits.route';

@NgModule({
    imports: [FahrschuleAsaSharedModule, RouterModule.forChild([auditsRoute])],
    declarations: [AuditsComponent]
})
export class AuditsModule {
}
