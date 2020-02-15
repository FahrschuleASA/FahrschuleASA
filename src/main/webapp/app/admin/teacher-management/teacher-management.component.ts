import {Component, OnInit, OnDestroy} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {Subscription} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {ActivatedRoute, Router} from '@angular/router';
import {JhiEventManager, JhiParseLinks, JhiAlertService} from 'ng-jhipster';

import {UserService} from "../../core/user/user.service";
import {User} from "../../core/user/user.model";
import {ITEMS_PER_PAGE} from 'app/shared/constants/pagination.constants';
import {AccountService} from 'app/core/auth/account.service';
import {Teacher} from "../../shared/model/teacher.model";
import {TeacherService} from "../../entities/teacher/teacher.service";
import {TeacherManagementDeleteDialogComponent} from './teacher-management-delete-dialog.component';

@Component({
    selector: 'jhi-teacher-mgmt',
    templateUrl: './teacher-management.component.html'
})
export class TeacherManagementComponent implements OnInit, OnDestroy {
    currentAccount: any;
    teachers: Teacher[];
    error: any;
    success: any;
    teacherListSubscription: Subscription;
    routeData: Subscription;
    links: any;
    totalItems: any;
    itemsPerPage: any;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;

    constructor(
        private userService: UserService,
        private teacherService: TeacherService,
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
            this.registerChangeInTeachers();
        });
    }

    ngOnDestroy() {
        this.routeData.unsubscribe();
        if (this.teacherListSubscription) {
            this.eventManager.destroy(this.teacherListSubscription);
        }
    }

    registerChangeInTeachers() {
        this.teacherListSubscription = this.eventManager.subscribe('teacherListModification', response => this.loadAll());
    }

    loadAll() {
        this.teacherService
            .query({
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe((res: HttpResponse<Teacher[]>) => this.onSuccess(res.body), (res: HttpResponse<any>) => this.onError(res.body));
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

    deleteTeacher(teacher: Teacher) {
        const modalRef = this.modalService.open(TeacherManagementDeleteDialogComponent, {size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.teacher = teacher;
    }

    private onSuccess(data) {
        this.teachers = data;
        this.totalItems = data.length;
    }

    private onError(error) {
        this.alertService.error(error.error, error.message, null);
    }
}
