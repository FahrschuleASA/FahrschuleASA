import {Route} from '@angular/router';

import {ActivateEmailComponent} from './activate-email.component';

export const activateEmailRoute: Route = {
    path: 'activate/email',
    component: ActivateEmailComponent,
    data: {
        authorities: [],
        pageTitle: 'activate.title'
    }
};
