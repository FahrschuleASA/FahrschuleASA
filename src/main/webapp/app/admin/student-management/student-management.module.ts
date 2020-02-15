import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {FahrschuleAsaSharedModule} from 'app/shared/shared.module';
import {StudentManagementComponent} from './student-management.component';
import {StudentManagementDetailComponent} from './student-management-detail.component';
import {StudentManagementUpdateComponent} from './student-management-update.component';
import {StudentManagementDeleteDialogComponent} from './student-management-delete-dialog.component';
import {studentManagementRoute} from './student-management.route';
import {StudentManagementCreateComponent} from "./student-management-create.component";

@NgModule({
    imports: [FahrschuleAsaSharedModule, RouterModule.forChild(studentManagementRoute)],
    declarations: [
        StudentManagementComponent,
        StudentManagementDetailComponent,
        StudentManagementUpdateComponent,
        StudentManagementCreateComponent,
        StudentManagementDeleteDialogComponent
],
    entryComponents: [StudentManagementDeleteDialogComponent]
})
export class StudentManagementModule {
}
