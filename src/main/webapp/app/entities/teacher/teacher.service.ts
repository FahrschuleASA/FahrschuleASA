import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DatePipe } from '@angular/common';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITeacher } from 'app/shared/model/teacher.model';

type EntityResponseType = HttpResponse<ITeacher>;
type EntityArrayResponseType = HttpResponse<ITeacher[]>;

@Injectable({ providedIn: 'root' })
export class TeacherService {
  public resourceUrl = SERVER_API_URL + 'api/teachers';

  constructor(
      protected http: HttpClient,
      private datepipe: DatePipe
  ) {}

  fetchCorrespondingTeacher(): Observable<ITeacher> {
      return this.http.get<ITeacher>(SERVER_API_URL + 'api/teacher/student');
  }

  create(teacher: ITeacher): Observable<EntityResponseType> {
    return this.http.post<ITeacher>(this.resourceUrl + '/create', teacher, { observe: 'response' });
  }

  update(teacher: ITeacher): Observable<EntityResponseType> {
    return this.http.put<ITeacher>(this.resourceUrl, teacher, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeacher>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeacher[]>(this.resourceUrl, { params: options, observe: 'response' });
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
