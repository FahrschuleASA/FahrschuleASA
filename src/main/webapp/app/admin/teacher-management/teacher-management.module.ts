import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {FahrschuleAsaSharedModule} from 'app/shared/shared.module';
import {TeacherManagementComponent} from './teacher-management.component';
import {TeacherManagementDetailComponent} from './teacher-management-detail.component';
import {TeacherManagementUpdateComponent} from './teacher-management-update.component';
import {TeacherManagementDeleteDialogComponent} from './teacher-management-delete-dialog.component';
import {teacherManagementRoute} from './teacher-management.route';
import {TeacherManagementCreateComponent} from "./teacher-management-create.component";

@NgModule({
    imports: [FahrschuleAsaSharedModule, RouterModule.forChild(teacherManagementRoute)],
    declarations: [
        TeacherManagementComponent,
        TeacherManagementDetailComponent,
        TeacherManagementUpdateComponent,
        TeacherManagementCreateComponent,
        TeacherManagementDeleteDialogComponent
],
    entryComponents: [TeacherManagementDeleteDialogComponent]
})
export class TeacherManagementModule {
}
