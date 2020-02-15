import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes} from '@angular/router';
import {JhiResolvePagingParams} from 'ng-jhipster';

import {StudentManagementComponent} from './student-management.component';
import {StudentManagementDetailComponent} from './student-management-detail.component';
import {StudentManagementUpdateComponent} from './student-management-update.component';
import {StudentManagementCreateComponent} from "./student-management-create.component";
import {StudentService} from "../../entities/student/student.service";
import {Student} from "../../shared/model/student.model";

@Injectable({providedIn: 'root'})
export class StudentManagementResolve implements Resolve<any> {
    constructor(private service: StudentService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id);
        }
        return new Student();
    }
}

export const studentManagementRoute: Routes = [
    {
        path: '',
        component: StudentManagementComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            pageTitle: 'studentManagement.home.title',
            defaultSort: 'id,asc'
        }
    },
    {
        path: ':id/view',
        component: StudentManagementDetailComponent,
        resolve: {
            student: StudentManagementResolve
        },
        data: {
            pageTitle: 'studentManagement.home.title'
        }
    },
    {
        path: ':id/edit',
        component: StudentManagementUpdateComponent,
        resolve: {
            student: StudentManagementResolve
        }
    },
    {
        path: 'new',
        component: StudentManagementCreateComponent
    }
];
