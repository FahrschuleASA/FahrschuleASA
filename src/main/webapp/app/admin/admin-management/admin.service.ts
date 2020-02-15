import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import { DatePipe } from '@angular/common';

import {SERVER_API_URL} from 'app/app.constants';
import {createRequestOption} from "../../shared/util/request-util";
import {IMyAccount} from "../../shared/model/my-account.model";
import {TeacherService} from "../../entities/teacher/teacher.service";

type EntityResponseType = HttpResponse<IMyAccount>;
type EntityArrayResponseType = HttpResponse<IMyAccount[]>;

@Injectable({providedIn: 'root'})
export class AdminService {
    public resourceUrl = SERVER_API_URL + 'api/admins';

    constructor(
        private http: HttpClient,
        private datePipe: DatePipe,
        private teacherService: TeacherService
    ) {
    }

    fetch(): Observable<IMyAccount> {
        return this.http.get<IMyAccount>(SERVER_API_URL + 'api/administrator');
    }

    create(account: IMyAccount): Observable<EntityResponseType> {
        return this.http.post<IMyAccount>(this.resourceUrl + '/create', account, { observe: 'response' });
    }

    update(account: IMyAccount): Observable<EntityResponseType> {
        return this.http.put<IMyAccount>(SERVER_API_URL + 'api/administrators', account, { observe: 'response' });
    }

    delete(id: number): Observable<IMyAccount> {
        return this.http.delete<IMyAccount>(`${SERVER_API_URL + 'api/administrators'}/${id}`);
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IMyAccount[]>(SERVER_API_URL + 'api/administrators', { params: options, observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IMyAccount>(`${SERVER_API_URL + 'api/administrators'}/${id}`, { observe: 'response' });
    }

    transformDateForDB(dateString){
        if (!dateString){
            return null;
        }
        let transformDate = dateString.replace(/(\d{2}).(\d{2}).(\d{4})/, "$3-$2-$1");
        return transformDate;
    }

    transformDateForFrontend(date){
        if (!date){
            return null;
        }
        let transformDate: string;
        transformDate = this.datePipe.transform(date, 'dd.MM.yyyy');
        return transformDate;
    }

}
