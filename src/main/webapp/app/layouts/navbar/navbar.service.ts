import {Injectable} from "@angular/core";
import { Subject } from 'rxjs';

@Injectable({providedIn: 'root'})
export class NavbarService {

    private heightSubject: Subject<number> = new Subject<number>();
    private currentHeight: number;

    constructor() {}

    public setNavbarHeight(height: number): void {
        if (height != this.currentHeight) {
            this.currentHeight = height;
            this.heightSubject.next(height);
        }
    }

    public getNavbarHeight(): Subject<number> {
        return this.heightSubject;
    }

}
