import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CalendarModule} from 'angular-calendar';

import {FahrschuleAsaSharedModule} from 'app/shared/shared.module';
import {HOME_ROUTES} from './home.route';
import {HomeComponent} from './home.component';
import {CustomHeaderModule} from "app/home/calendar-utils/header.module";
import {TheoryLessonModalComponent} from "app/home/modals/theory-lesson/theory-lesson.modal.component";
import {BrowserModule} from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { SlideMenuComponent } from './slide-menu/slide-menu.component';
import {LocationModalComponent} from './modals/location/location.modal.component';
import {ScrollingModule} from '@angular/cdk/scrolling';

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
import { CommonModule } from '@angular/common';
import {BookLessonModalComponent} from "app/home/modals/book-lesson/book-lesson.modal.component";
import {DrivingLessonModalComponent} from "app/home/modals/driving-lesson/driving-lesson.modal.component";
import {CalendarService} from "app/home/calendar-utils/calendar.service";

@NgModule({
    imports: [
        FahrschuleAsaSharedModule,
        RouterModule.forChild(HOME_ROUTES),
        CalendarModule,
        CustomHeaderModule,
        BrowserModule,
        NgbModule,
        BrowserAnimationsModule,
        ScrollingModule,
        CommonModule,

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
        ScrollingModule,
    ],

    declarations: [HomeComponent, TheoryLessonModalComponent, SlideMenuComponent, LocationModalComponent, BookLessonModalComponent, DrivingLessonModalComponent],
    entryComponents: [TheoryLessonModalComponent, LocationModalComponent, BookLessonModalComponent, DrivingLessonModalComponent],
    providers: [CalendarService]
})
export class FahrschuleAsaHomeModule {
}
