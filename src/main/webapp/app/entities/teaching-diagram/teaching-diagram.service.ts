import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITeachingDiagram } from 'app/shared/model/teaching-diagram.model';

type EntityResponseType = HttpResponse<ITeachingDiagram>;
type EntityArrayResponseType = HttpResponse<ITeachingDiagram[]>;

@Injectable({ providedIn: 'root' })
export class TeachingDiagramService {
  public resourceUrl = SERVER_API_URL + 'api/teaching-diagrams';

  constructor(protected http: HttpClient) {}

  fetch(): Observable<ITeachingDiagram> {
      return this.http.get<ITeachingDiagram>(this.resourceUrl);
      // For testing purposes, as long as backend does not provide methods
      // const teachingDiagram = new TeachingDiagram( 1912, 13, 1,1,1,1,1,1 );
      // return of(teachingDiagram);
  }

  fetchEducationData(): Observable<ITeachingDiagram> {
      return this.http.get<ITeachingDiagram>(SERVER_API_URL + "api/education-data");
  }

  findEducationData(id: number): Observable<ITeachingDiagram> {
      return this.http.get<ITeachingDiagram>(`${SERVER_API_URL + "api/education-data"}/${id}`);
  }

  saveEducationData(educationData: ITeachingDiagram): Observable<EntityResponseType> {
      return this.http.put<ITeachingDiagram>(SERVER_API_URL + "api/education-data", educationData, { observe: 'response' });
  }

  create(teachingDiagram: ITeachingDiagram): Observable<EntityResponseType> {
    return this.http.post<ITeachingDiagram>(this.resourceUrl, teachingDiagram, { observe: 'response' });
  }

  update(teachingDiagram: ITeachingDiagram): Observable<EntityResponseType> {
    return this.http.put<ITeachingDiagram>(this.resourceUrl, teachingDiagram, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeachingDiagram>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeachingDiagram[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
