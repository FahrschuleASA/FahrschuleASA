import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITheoryLesson } from 'app/shared/model/theory-lesson.model';

type EntityResponseType = HttpResponse<ITheoryLesson>;
type EntityArrayResponseType = HttpResponse<ITheoryLesson[]>;

@Injectable({ providedIn: 'root' })
export class TheoryLessonService {
  public resourceUrl = SERVER_API_URL + 'api/theory-lessons';

  constructor(protected http: HttpClient) {}

  create(theoryLesson: ITheoryLesson): Observable<EntityResponseType> {
    return this.http.post<ITheoryLesson>(this.resourceUrl, theoryLesson, { observe: 'response' });
  }

  update(theoryLesson: ITheoryLesson): Observable<EntityResponseType> {
    return this.http.put<ITheoryLesson>(this.resourceUrl, theoryLesson, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITheoryLesson>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITheoryLesson[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
