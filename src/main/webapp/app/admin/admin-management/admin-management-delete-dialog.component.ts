import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';

import {Teacher} from "../../shared/model/teacher.model";
import {AdminService} from "app/admin/admin-management/admin.service";

@Component({
    selector: 'jhi-admin-mgmt-delete-dialog',
    templateUrl: './admin-management-delete-dialog.component.html'
})
export class AdminManagementDeleteDialogComponent {
    admin: Teacher;

    constructor(private adminService: AdminService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id) {
        this.adminService.delete(id).subscribe(response => {
            this.eventManager.broadcast({name: 'adminListModification', content: 'Deleted an admin'});
            this.activeModal.close(true);
        });
    }
}
