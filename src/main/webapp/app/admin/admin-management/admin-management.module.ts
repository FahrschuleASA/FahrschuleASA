import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {FahrschuleAsaSharedModule} from 'app/shared/shared.module';
import {AdminManagementComponent} from './admin-management.component';
import {AdminManagementDetailComponent} from './admin-management-detail.component';
import {AdminManagementUpdateComponent} from './admin-management-update.component';
import {AdminManagementDeleteDialogComponent} from './admin-management-delete-dialog.component';
import {adminManagementRoute} from './admin-management.route';
import {AdminManagementCreateComponent} from "./admin-management-create.component";

@NgModule({
    imports: [FahrschuleAsaSharedModule, RouterModule.forChild(adminManagementRoute)],
    declarations: [
        AdminManagementComponent,
        AdminManagementDetailComponent,
        AdminManagementUpdateComponent,
        AdminManagementCreateComponent,
        AdminManagementDeleteDialogComponent
],
    entryComponents: [AdminManagementDeleteDialogComponent]
})
export class AdminManagementModule {
}
