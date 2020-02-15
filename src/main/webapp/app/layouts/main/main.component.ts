import {Component, OnInit, ElementRef, ViewChild, AfterViewInit, Renderer2} from '@angular/core';
import {Router, ActivatedRouteSnapshot, NavigationEnd, NavigationError} from '@angular/router';

import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {AccountService} from "app/core/auth/account.service";
import {NavbarService} from "app/layouts/navbar/navbar.service";

@Component({
    selector: 'jhi-main',
    templateUrl: './main.component.html',
    styleUrls: ["main.scss"]
})
export class JhiMainComponent implements OnInit, AfterViewInit {

    @ViewChild("maincontent", {static: true})
    private content: ElementRef;

    constructor(private jhiLanguageHelper: JhiLanguageHelper,
                private router: Router,
                private accountService: AccountService,
                private navbarService: NavbarService,
                private renderer: Renderer2) {
    }

    private getPageTitle(routeSnapshot: ActivatedRouteSnapshot) {
        let title: string = routeSnapshot.data && routeSnapshot.data['pageTitle'] ? routeSnapshot.data['pageTitle'] : 'fahrschuleAsaApp';
        if (routeSnapshot.firstChild) {
            title = this.getPageTitle(routeSnapshot.firstChild) || title;
        }
        return title;
    }

    ngOnInit() {
        this.router.events.subscribe(event => {
            if (event instanceof NavigationEnd) {
                this.jhiLanguageHelper.updateTitle(this.getPageTitle(this.router.routerState.snapshot.root));
            }
            if (event instanceof NavigationError && event.error.status === 404) {
                this.router.navigate(['/404']);
            }
        });


    }

    ngAfterViewInit(): void {
        this.navbarService.getNavbarHeight().subscribe(height => {
            console.log(`Set height to ${height}px`);
            console.log(this.renderer);
            console.log(this.content);
            this.renderer.setStyle(this.content.nativeElement, "min-height", `calc(100vh - 50px - ${height}px`);
        })
    }

}
