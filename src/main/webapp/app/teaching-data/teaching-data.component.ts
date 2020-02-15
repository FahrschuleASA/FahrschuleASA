import {Component, OnInit} from '@angular/core';
import {TeachingDiagramService} from "../entities/teaching-diagram/teaching-diagram.service";
import {AccountService} from "../core/auth/account.service";
import {Account} from "../core/user/account.model";
import {ITeachingDiagram} from "../shared/model/teaching-diagram.model";
import {TeacherService} from "../entities/teacher/teacher.service";
import {Teacher} from "../shared/model/teacher.model";


@Component({
    selector: 'jhi-teaching-data',
    templateUrl: './teaching-data.component.html',
    styleUrls: ['./teaching-data.component.scss']
})
export class TeachingDataComponent implements OnInit {
    teacher: Teacher;
    currentAccount: Account;
    teachingDiagram: ITeachingDiagram;

    constructor(
        private teachingDiagramService: TeachingDiagramService,
        private teacherService: TeacherService,
        private accountService: AccountService
    ) {}

    ngOnInit() {
        this.accountService.identity().subscribe(account => {
            this.currentAccount = account;
        });
        this.teachingDiagramService.fetchEducationData().subscribe( teachingDiagram => {
            this.teachingDiagram = teachingDiagram;
        });
        this.teacherService.fetchCorrespondingTeacher().subscribe( teacher => {
            this.teacher = teacher;
        });
    }

    get_type(value): string {
        if (value >= 100){
            return "success";
        }else if(value >= 50){
            return "secondary";
        }else{
            return "warning";
        }
    }
}
