import {Component, OnInit, OnDestroy} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {Subscription} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {ActivatedRoute, Router} from '@angular/router';
import {JhiEventManager, JhiParseLinks, JhiAlertService} from 'ng-jhipster';

import {ITEMS_PER_PAGE} from 'app/shared/constants/pagination.constants';
import {AccountService} from 'app/core/auth/account.service';
import {UserService} from 'app/core/user/user.service';
import {User} from 'app/core/user/user.model';
import {StudentManagementDeleteDialogComponent} from './student-management-delete-dialog.component';
import {StudentService} from "app/entities/student/student.service";
import {Student} from "app/shared/model/student.model";

@Component({
    selector: 'jhi-student-mgmt',
    templateUrl: './student-management.component.html',
    styleUrls: ['./student-management.scss']
})
export class StudentManagementComponent implements OnInit, OnDestroy {
    isAdmin: boolean;
    showAllStudents = false;
    showActiveStudents = true;
    currentAccount: any;
    students: Student[];
    error: any;
    success: any;
    studentListSubscription: Subscription;
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
        private studentService: StudentService,
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
        this.isAdmin = this.accountService.hasAnyAuthority(['ROLE_ADMIN']);
        this.accountService.identity().subscribe(account => {
            this.currentAccount = account;
            this.loadStudents(this.isAdmin);
            this.registerChangeInStudents();
        });

    }

    ngOnDestroy() {
        this.routeData.unsubscribe();
        if (this.studentListSubscription) {
            this.eventManager.destroy(this.studentListSubscription);
        }
    }

    registerChangeInStudents() {
        this.studentListSubscription = this.eventManager.subscribe('studentListModification', response => this.loadStudents(this.showAllStudents || this.isAdmin));
    }

    loadStudents(all: boolean) {

        if (all){
            this.studentService
                .query({
                    page: this.page - 1,
                    size: this.itemsPerPage,
                    sort: this.sort(),
                    active_only: this.showActiveStudents
                })
                .subscribe((res: HttpResponse<Student[]>) => this.onSuccess(res.body, res.headers), (res: HttpResponse<any>) => this.onError(res.body));

        } else{
            this.studentService
                .queryStudentsForTeacher({
                    page: this.page - 1,
                    size: this.itemsPerPage,
                    sort: this.sort(),
                    active_only: this.showActiveStudents
                })
                .subscribe((res: HttpResponse<Student[]>) => this.onSuccess(res.body, res.headers), (res: HttpResponse<any>) => this.onError(res.body));
        }
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
        this.loadStudents(this.showAllStudents || this.isAdmin);
    }

    toggleAllStudents(){
        this.showAllStudents = !this.showAllStudents;
        this.loadStudents(this.showAllStudents || this.isAdmin);
    }

    toggleActiveStudents(){
        this.showActiveStudents = !this.showActiveStudents;
        this.loadStudents(this.showAllStudents || this.isAdmin);
    }

    deleteStudent(student: Student) {
        const modalRef = this.modalService.open(StudentManagementDeleteDialogComponent, {size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.student = student;
    }

    private onSuccess(data, headers) {
        this.students = data;
        this.totalItems = this.students.length;
    }

    private onError(error) {
        this.alertService.error(error.error, error.message, null);
    }
}
