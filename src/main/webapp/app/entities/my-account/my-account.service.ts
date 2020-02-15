import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { map } from 'rxjs/operators';
import { DatePipe } from '@angular/common';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import {IMyAccount, MyAccount} from 'app/shared/model/my-account.model';

type EntityResponseType = HttpResponse<IMyAccount>;
type EntityArrayResponseType = HttpResponse<IMyAccount[]>;

@Injectable({ providedIn: 'root' })
export class MyAccountService {
    public resourceUrl = SERVER_API_URL + 'api/my-account';

    constructor(
        protected http: HttpClient,
        private datePipe: DatePipe
    ) {}

    create(myAccount: IMyAccount): Observable<EntityResponseType> {
        const copy = myAccount;
        return this.http
            .post<IMyAccount>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => res));
    }

    update(myAccount: IMyAccount): Observable<EntityResponseType> {
        const copy = myAccount;
        return this.http
            .put<IMyAccount>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => res));
    }

    updateTeacher(myAccount: IMyAccount): Observable<EntityResponseType> {
            const copy = myAccount;
            return this.http
                    .put<IMyAccount>(SERVER_API_URL + 'api/teachers/settings', copy, { observe: 'response' })
                    .pipe(map((res: EntityResponseType) => res));
    }

    updateStudent(myAccount: IMyAccount): Observable<EntityResponseType> {
        const copy = myAccount;
        return this.http
            .put<IMyAccount>(this.resourceUrl + '/student', copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => res));
    }

    fetch(): Observable<MyAccount> {
        return this.http.get<MyAccount>(this.resourceUrl);
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IMyAccount>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => res));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IMyAccount[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, {observe: 'response'});
    }

    transformDateForDB(dateString){
        if (!dateString){
                return null;
        }
        let transformDate = dateString.replace(/(\d{2}).(\d{2}).(\d{4})/, "$3-$2-$1");
        // let date = new Date(Date.parse(transformdate));
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


    // protected convertDateFromClient(myAccount: IMyAccount): IMyAccount {
    //   const copy: IMyAccount = Object.assign({}, myAccount, {
    //       birthdate: myAccount.birthdate != null && myAccount.birthdate.isValid() ? myAccount.birthdate.format(DATE_FORMAT) : null
    //   });
    //   return copy;
    // }
    //
    // protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    //   if (res.body) {
    //       res.body.birthdate = res.body.birthdate != null ? res.body.birthdate : null;
    //   }
    //   return res;
    // }
    //
    // protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    //   if (res.body) {
    //       res.body.forEach((myAccount: IMyAccount) => {
    //           myAccount.birthdate = myAccount.birthdate != null ? (myAccount.birthdate) : null;
    //       });
    //   }
    //   return res;
    // }
}
