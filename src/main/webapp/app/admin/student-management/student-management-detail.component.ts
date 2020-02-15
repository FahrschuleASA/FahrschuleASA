import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {Student} from "../../shared/model/student.model";
import {StudentService} from "../../entities/student/student.service";
import {TeachingDiagramService} from "../../entities/teaching-diagram/teaching-diagram.service";
import {TeachingDiagram} from "../../shared/model/teaching-diagram.model";

@Component({
    selector: 'jhi-student-mgmt-detail',
    templateUrl: './student-management-detail.component.html',
    styleUrls: ['student-management.scss']
})
export class StudentManagementDetailComponent implements OnInit {
    student: Student;
    birthdate: string;
    teachingDiagram: TeachingDiagram;

    constructor(
        private route: ActivatedRoute,
        private studentService: StudentService,
        private teachingDiagramService: TeachingDiagramService
    ) {
    }

    ngOnInit() {
        this.route.data.subscribe(({student}) => {
            this.student = student.body ? student.body : student;
        });
        this.teachingDiagramService.findEducationData(this.student.id).subscribe(
            (teachingDiagram) => {
                this.teachingDiagram = teachingDiagram;
        });
        this.birthdate = this.studentService.transformDateForFrontend(this.student.birthdate);
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
