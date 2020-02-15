import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes} from '@angular/router';
import {JhiResolvePagingParams} from 'ng-jhipster';

import {AdminManagementComponent} from './admin-management.component';
import {AdminManagementDetailComponent} from './admin-management-detail.component';
import {AdminManagementUpdateComponent} from './admin-management-update.component';
import {AdminManagementCreateComponent} from "./admin-management-create.component";
import {MyAccount} from "../../shared/model/my-account.model";
import {AdminService} from "./admin.service";

@Injectable({providedIn: 'root'})
export class AdminManagementResolve implements Resolve<any> {
    constructor(private service: AdminService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id);
        }
        return new MyAccount();
    }
}

export const adminManagementRoute: Routes = [
    {
        path: '',
        component: AdminManagementComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            pageTitle: 'fahrschuleAsaApp.adminManagement.home.title',
            defaultSort: 'id,asc'
        }
    },
    {
        path: ':id/view',
        component: AdminManagementDetailComponent,
        resolve: {
            admin: AdminManagementResolve
        },
        data: {
            pageTitle: 'fahrschuleAsaApp.adminManagement.home.title'
        }
    },
    {
        path: ':id/edit',
        component: AdminManagementUpdateComponent,
        resolve: {
            admin: AdminManagementResolve
        }
    },
    {
        path: 'new',
        component: AdminManagementCreateComponent
    }
];
