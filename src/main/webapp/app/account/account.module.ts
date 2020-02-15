import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {FahrschuleAsaSharedModule} from 'app/shared/shared.module';

import {PasswordStrengthBarComponent} from './password/password-strength-bar.component';
import {RegisterComponent} from './register/register.component';
import {ActivateComponent} from './activate/activate.component';
import {PasswordComponent} from './password/password.component';
import {PasswordResetInitComponent} from './password-reset/init/password-reset-init.component';
import {PasswordResetFinishComponent} from './password-reset/finish/password-reset-finish.component';
import {PersonalDataComponent} from "./personal-data/personal-data.component";
import {EditPersonalDataComponent} from "./edit-personal-data/edit-personal-data.component";
import {StudentSettingsComponent} from './student-settings/student-settings.component';
import {accountState} from './account.route';
import {TeachingDataComponent} from "../teaching-data/teaching-data.component";
import {SettingsComponent} from "./settings/settings.component";
import {ActivateEmailComponent} from "app/account/activate-email/activate-email.component";
import {PreferencesComponent} from "app/account/preferences/preferences.component";
import {CalendarModule} from 'angular-calendar';
import {CommonModule} from '@angular/common';
import {DoubleLocationModalComponent} from "app/account/preferences/modals/double-location.modal.component";
import {TimeSlotsComponent} from "app/account/time-slots/time-slots.component";
import {CalendarHeaderComponent} from "app/account/time-slots/calendar-utils/calendar-header.component";
import {TimeSlotModalComponent} from "app/account/time-slots/modals/time-slot.modal.component";

import {A11yModule} from '@angular/cdk/a11y';
import {DragDropModule} from '@angular/cdk/drag-drop';
import {PortalModule} from '@angular/cdk/portal';
import {CdkStepperModule} from '@angular/cdk/stepper';
import {CdkTableModule} from '@angular/cdk/table';
import {CdkTreeModule} from '@angular/cdk/tree';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatBadgeModule} from '@angular/material/badge';
import {MatBottomSheetModule} from '@angular/material/bottom-sheet';
import {MatButtonModule} from '@angular/material/button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatStepperModule} from '@angular/material/stepper';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatMenuModule} from '@angular/material/menu';
import {MatNativeDateModule, MatRippleModule} from '@angular/material/core';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSliderModule} from '@angular/material/slider';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatTreeModule} from '@angular/material/tree';
import {ScrollingModule} from '@angular/cdk/scrolling';


@NgModule({
    imports: [FahrschuleAsaSharedModule, RouterModule.forChild(accountState), CalendarModule, CommonModule,
        A11yModule,
        CdkStepperModule,
        CdkTableModule,
        CdkTreeModule,
        DragDropModule,
        MatAutocompleteModule,
        MatBadgeModule,
        MatBottomSheetModule,
        MatButtonModule,
        MatButtonToggleModule,
        MatCardModule,
        MatCheckboxModule,
        MatChipsModule,
        MatStepperModule,
        MatDatepickerModule,
        MatDialogModule,
        MatDividerModule,
        MatExpansionModule,
        MatGridListModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        MatMenuModule,
        MatNativeDateModule,
        MatPaginatorModule,
        MatProgressBarModule,
        MatProgressSpinnerModule,
        MatRadioModule,
        MatRippleModule,
        MatSelectModule,
        MatSidenavModule,
        MatSliderModule,
        MatSlideToggleModule,
        MatSnackBarModule,
        MatSortModule,
        MatTableModule,
        MatTabsModule,
        MatToolbarModule,
        MatTooltipModule,
        MatTreeModule,
        PortalModule,
        ScrollingModule],
    declarations: [
        ActivateComponent,
        RegisterComponent,
        PasswordComponent,
        PasswordStrengthBarComponent,
        PasswordResetInitComponent,
        PasswordResetFinishComponent,
        PersonalDataComponent,
        EditPersonalDataComponent,
        SettingsComponent,
        StudentSettingsComponent,
        TeachingDataComponent,
        ActivateEmailComponent,
        PreferencesComponent,
        TimeSlotsComponent,
        DoubleLocationModalComponent,
        TimeSlotModalComponent,
        CalendarHeaderComponent
    ],
    entryComponents: [DoubleLocationModalComponent, TimeSlotModalComponent]
})
export class FahrschuleAsaAccountModule {
}
