import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IDrivingLesson } from 'app/shared/model/driving-lesson.model';

type EntityResponseType = HttpResponse<IDrivingLesson>;
type EntityArrayResponseType = HttpResponse<IDrivingLesson[]>;

@Injectable({ providedIn: 'root' })
export class DrivingLessonService {
  public resourceUrl = SERVER_API_URL + 'api/driving-lessons';

  constructor(protected http: HttpClient) {}

  create(drivingLesson: IDrivingLesson): Observable<EntityResponseType> {
    return this.http.post<IDrivingLesson>(this.resourceUrl, drivingLesson, { observe: 'response' });
  }

  update(drivingLesson: IDrivingLesson): Observable<EntityResponseType> {
    return this.http.put<IDrivingLesson>(this.resourceUrl, drivingLesson, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDrivingLesson>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDrivingLesson[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
