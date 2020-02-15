import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FahrschuleAsaSharedModule} from 'app/shared/shared.module';

import {JhiMetricsMonitoringComponent} from './metrics.component';

import {metricsRoute} from './metrics.route';

@NgModule({
    imports: [FahrschuleAsaSharedModule, RouterModule.forChild([metricsRoute])],
    declarations: [JhiMetricsMonitoringComponent]
})
export class MetricsModule {
}
