import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {AdminService} from "./admin.service";
import {MyAccount} from "../../shared/model/my-account.model";

@Component({
    selector: 'jhi-admin-mgmt-detail',
    templateUrl: './admin-management-detail.component.html',
    styleUrls: ['./admin-management.scss']
})
export class AdminManagementDetailComponent implements OnInit {
    admin: MyAccount;
    birthdate: string;

    constructor(
        private route: ActivatedRoute,
        private adminService: AdminService
    ) {
    }

    ngOnInit() {
        this.route.data.subscribe(({admin}) => {
            this.admin = admin.body ? admin.body : admin;
        });
        this.birthdate = this.adminService.transformDateForFrontend(this.admin.birthdate);
    }
}
