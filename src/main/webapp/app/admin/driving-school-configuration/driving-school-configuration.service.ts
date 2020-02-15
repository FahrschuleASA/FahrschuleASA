import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

import {SERVER_API_URL} from 'app/app.constants';
import {Configuration} from 'app/shared/model/configuration.model';

@Injectable({providedIn: 'root'})
export class DrivingSchoolConfigurationService {

    constructor(
        private http: HttpClient,
    ) {
    }

    fetch(): Observable<Configuration> {
        return this.http.get<Configuration>(SERVER_API_URL + 'api/configuration');
    }

    save(configuration: Configuration): Observable<Configuration> {
        return this.http.put<Configuration>(SERVER_API_URL + 'api/configuration', configuration);
    }

    getMaxInactive(): Observable<number> {
        return this.http.get<number>(SERVER_API_URL + 'api/configuration/max-inactive');
    }

    fetchAvailableDrivingCategories(): Observable<string[]> {
        return this.http.get<string[]>(SERVER_API_URL + 'api/driving-categories');
    }
}
