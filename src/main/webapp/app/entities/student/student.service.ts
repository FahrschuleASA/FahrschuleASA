import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DatePipe } from '@angular/common';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IStudent } from 'app/shared/model/student.model';

type EntityResponseType = HttpResponse<IStudent>;
type EntityArrayResponseType = HttpResponse<IStudent[]>;

@Injectable({ providedIn: 'root' })
export class StudentService {
  public resourceUrl = SERVER_API_URL + 'api/students';

  constructor(
      protected http: HttpClient,
      private datepipe: DatePipe
  ) {}

  create(student: IStudent): Observable<EntityResponseType> {
    return this.http.post<IStudent>(this.resourceUrl + '/create', student, { observe: 'response' });
  }

  update(student: IStudent): Observable<EntityResponseType> {
    return this.http.put<IStudent>(this.resourceUrl, student, { observe: 'response' });
  }

  updateSettings(student: IStudent): Observable<EntityResponseType> {
      return this.http.put<IStudent>(this.resourceUrl + '/settings', student, { observe: 'response' });
  }

  fetch(): Observable<IStudent> {
      return this.http.get<IStudent>(SERVER_API_URL + 'api/student');
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IStudent>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IStudent[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  queryStudentsForTeacher(req?: any): Observable<EntityArrayResponseType> {
      const options = createRequestOption(req);
      return this.http.get<IStudent[]>(this.resourceUrl + "/teacher", { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
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
      let transformdate: string;
      transformdate = this.datepipe.transform(date, 'dd.MM.yyyy');
      return transformdate;
  }
}
