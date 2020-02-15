import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';

import {Teacher} from "../../shared/model/teacher.model";
import {TeacherService} from "../../entities/teacher/teacher.service";

@Component({
    selector: 'jhi-teacher-mgmt-delete-dialog',
    templateUrl: './teacher-management-delete-dialog.component.html'
})
export class TeacherManagementDeleteDialogComponent {
    teacher: Teacher;

    constructor(private teacherService: TeacherService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id) {
        this.teacherService.delete(id).subscribe(response => {
            this.eventManager.broadcast({name: 'teacherListModification', content: 'Deleted a teacher'});
            this.activeModal.close(true);
        });
    }
}
