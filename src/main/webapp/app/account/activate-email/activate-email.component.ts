import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {flatMap} from 'rxjs/operators';

import {ActivateEmailService} from './activate-email.service';

@Component({
    selector: 'jhi-activate-email',
    templateUrl: './activate-email.component.html'
})
export class ActivateEmailComponent implements OnInit {
    error: string;
    success: string;

    constructor(private activateEmailService: ActivateEmailService, private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.route.queryParams.pipe(flatMap(params => this.activateEmailService.get(params.key))).subscribe(
            () => {
                this.error = null;
                this.success = 'OK';
            },
            () => {
                this.success = null;
                this.error = 'ERROR';
            }
        );
    }
}
