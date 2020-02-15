import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import './vendor';
import {FahrschuleAsaSharedModule} from 'app/shared/shared.module';
import {FahrschuleAsaCoreModule} from 'app/core/core.module';
import {FahrschuleAsaAppRoutingModule} from './app-routing.module';
import {FahrschuleAsaHomeModule} from './home/home.module';
import {FahrschuleAsaEntityModule} from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import {JhiMainComponent} from './layouts/main/main.component';
import {NavbarComponent} from './layouts/navbar/navbar.component';
import {FooterComponent} from './layouts/footer/footer.component';
import {PageRibbonComponent} from './layouts/profiles/page-ribbon.component';
import {ActiveMenuDirective} from './layouts/navbar/active-menu.directive';
import {ErrorComponent} from './layouts/error/error.component';
import {LoginscreenComponent} from './loginscreen/loginscreen.component';
import {CalendarModule, DateAdapter} from 'angular-calendar';
import {adapterFactory} from 'angular-calendar/date-adapters/date-fns';

@NgModule({
    imports: [
        BrowserModule,
        FahrschuleAsaSharedModule,
        FahrschuleAsaCoreModule,
        FahrschuleAsaHomeModule,
        // jhipster-needle-angular-add-module JHipster will add new module here
        FahrschuleAsaEntityModule,
        FahrschuleAsaAppRoutingModule,
        CalendarModule.forRoot({
            provide: DateAdapter,
            useFactory: adapterFactory
        }),
    ],
    declarations: [JhiMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent, LoginscreenComponent],
    bootstrap: [JhiMainComponent]
})
export class FahrschuleAsaAppModule {
}
