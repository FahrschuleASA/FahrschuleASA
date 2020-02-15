import {Component, OnInit, OnDestroy} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {Subscription} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {ActivatedRoute, Router} from '@angular/router';
import {JhiEventManager, JhiParseLinks, JhiAlertService} from 'ng-jhipster';

import {User} from "../../core/user/user.model";
import {ITEMS_PER_PAGE} from 'app/shared/constants/pagination.constants';
import {AccountService} from 'app/core/auth/account.service';
import {AdminManagementDeleteDialogComponent} from './admin-management-delete-dialog.component';
import {AdminService} from "./admin.service";
import {MyAccount} from "../../shared/model/my-account.model";

@Component({
    selector: 'jhi-admin-mgmt',
    templateUrl: './admin-management.component.html'
})
export class AdminManagementComponent implements OnInit, OnDestroy {
    currentAccount: any;
    admins: MyAccount[];
    error: any;
    success: any;
    adminListSubscription: Subscription;
    routeData: Subscription;
    links: any;
    totalItems: any;
    itemsPerPage: any;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;

    constructor(
        private adminService: AdminService,
        private alertService: JhiAlertService,
        private accountService: AccountService,
        private parseLinks: JhiParseLinks,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private eventManager: JhiEventManager,
        private modalService: NgbModal
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.routeData = this.activatedRoute.data.subscribe(data => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
    }

    ngOnInit() {
        this.accountService.identity().subscribe(account => {
            this.currentAccount = account;
            this.loadAll();
            this.registerChangeInAdmins();
        });
    }

    ngOnDestroy() {
        this.routeData.unsubscribe();
        if (this.adminListSubscription) {
            this.eventManager.destroy(this.adminListSubscription);
        }
    }

    registerChangeInAdmins() {
        this.adminListSubscription = this.eventManager.subscribe('adminListModification', response => this.loadAll());
    }

    loadAll() {
        this.adminService
            .query({
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe((res: HttpResponse<MyAccount[]>) => this.onSuccess(res.body), (res: HttpResponse<any>) => this.onError(res.body));
    }

    trackIdentity(index, item: User) {
        return item.id;
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }

    transition() {
        this.router.navigate(['./'], {
            relativeTo: this.activatedRoute.parent,
            queryParams: {
                page: this.page,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
            }
        });
        this.loadAll();
    }

    deleteAdmin(admin: MyAccount) {
        const modalRef = this.modalService.open(AdminManagementDeleteDialogComponent, {size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.admin = admin;
    }

    private onSuccess(data) {
        this.admins = data;
        this.totalItems = data.length;
    }

    private onError(error) {
        this.alertService.error(error.error, error.message, null);
    }
}
