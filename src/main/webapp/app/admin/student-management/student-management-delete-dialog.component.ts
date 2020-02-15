import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';

import {StudentService} from "../../entities/student/student.service";
import {Student} from "../../shared/model/student.model";

@Component({
    selector: 'jhi-student-mgmt-delete-dialog',
    templateUrl: './student-management-delete-dialog.component.html'
})
export class StudentManagementDeleteDialogComponent {
    student: Student;

    constructor(private studentService: StudentService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id) {
        this.studentService.delete(id).subscribe(response => {
            this.eventManager.broadcast({name: 'studentListModification', content: 'Deleted a student'});
            this.activeModal.close(true);
        });
    }
}
