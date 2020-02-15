import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes} from '@angular/router';
import {JhiResolvePagingParams} from 'ng-jhipster';

import {TeacherManagementComponent} from './teacher-management.component';
import {TeacherManagementDetailComponent} from './teacher-management-detail.component';
import {TeacherManagementUpdateComponent} from './teacher-management-update.component';
import {TeacherManagementCreateComponent} from "./teacher-management-create.component";
import {Teacher} from "../../shared/model/teacher.model";
import {TeacherService} from "../../entities/teacher/teacher.service";

@Injectable({providedIn: 'root'})
export class TeacherManagementResolve implements Resolve<any> {
    constructor(private service: TeacherService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id);
        }
        return new Teacher();
    }
}

export const teacherManagementRoute: Routes = [
    {
        path: '',
        component: TeacherManagementComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            pageTitle: 'teacherManagement.home.title',
            defaultSort: 'id,asc'
        }
    },
    {
        path: ':id/view',
        component: TeacherManagementDetailComponent,
        resolve: {
            teacher: TeacherManagementResolve
        },
        data: {
            pageTitle: 'teacherManagement.home.title'
        }
    },
    {
        path: ':id/edit',
        component: TeacherManagementUpdateComponent,
        resolve: {
            teacher: TeacherManagementResolve
        }
    },
    {
        path: 'new',
        component: TeacherManagementCreateComponent
    }
];
