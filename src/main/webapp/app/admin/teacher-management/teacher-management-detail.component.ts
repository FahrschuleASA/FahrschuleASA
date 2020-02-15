import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {Teacher} from "../../shared/model/teacher.model";
import {TeacherService} from "../../entities/teacher/teacher.service";

@Component({
    selector: 'jhi-teacher-mgmt-detail',
    templateUrl: './teacher-management-detail.component.html',
    styleUrls: ['./teacher-management.scss']
})
export class TeacherManagementDetailComponent implements OnInit {
    teacher: Teacher;
    birthdate: string;

    constructor(
        private route: ActivatedRoute,
        private teacherService: TeacherService
    ) {
    }

    ngOnInit() {
        this.route.data.subscribe(({teacher}) => {
            this.teacher = teacher.body ? teacher.body : teacher;
        });
        this.birthdate = this.teacherService.transformDateForFrontend(this.teacher.birthdate);
    }
}
