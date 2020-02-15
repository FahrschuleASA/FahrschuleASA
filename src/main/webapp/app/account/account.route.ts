import {Routes} from '@angular/router';

import {activateRoute} from './activate/activate.route';
import {passwordRoute} from './password/password.route';
import {passwordResetFinishRoute} from './password-reset/finish/password-reset-finish.route';
import {passwordResetInitRoute} from './password-reset/init/password-reset-init.route';
import {personalDataRoute} from './personal-data/personal-data.route';
import {editPersonalDataRoute} from './edit-personal-data/edit-personal-data.route';
import {registerRoute} from './register/register.route';
import {settingsRoute} from './settings/settings.route';
import {studentSettingsRoute} from './student-settings/student-settings.route';
import {teachingDataRoute} from 'app/teaching-data/teaching-data.route';
import {activateEmailRoute} from "app/account/activate-email/activate-email.route";
import {preferencesRoute} from "app/account/preferences/preferences.route";
import {timeSlotsRoute} from "app/account/time-slots/time-slots.route";

const ACCOUNT_ROUTES = [activateRoute, passwordRoute, passwordResetFinishRoute, passwordResetInitRoute, personalDataRoute, editPersonalDataRoute, registerRoute, settingsRoute, studentSettingsRoute, teachingDataRoute, activateEmailRoute, preferencesRoute, timeSlotsRoute];

export const accountState: Routes = [
    {
        path: '',
        children: ACCOUNT_ROUTES
    }
];
