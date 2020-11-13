import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { of as observableOf,  Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface ServiceUsage { [templateName: string]: PackageName }
type PackageName = string;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface Service {
  id?: number;
  name: string;
  description?: string;
  initScript?: string;
  packages?: string[];
  templates?: string[];
}

@Injectable()
export class ServiceHttpService {

  private _basePathURL = 'api/service';

  constructor(private http: HttpClient) { }

  public getServices(): Observable<Service[]> {
    return this.http.get<Service[]>(this._basePathURL);
  }

  public getService(serviceName: string): Observable<Service> {
    return this.http.get<Service>(`${this._basePathURL}/${serviceName}`);
  }

  public existsService(serviceName: string): Observable<boolean> {
    return this.getService(serviceName).pipe(
      map((service: Service) => (service !== undefined)),
      catchError(() => observableOf(false)),);
  }

  public getServiceNames(): Observable<string[]> {
    return this.getServices().pipe(map((services: Service[]) => services.map(s => s.name).sort()));
  }

  public getServiceUsage(serviceName: string): Observable<ServiceUsage> {
    return this.http.get<ServiceUsage>(`${this._basePathURL}/${serviceName}/usage`);
  }

  public deleteService(service: Service): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${service.name}`);
  }

  public save(service: Service): Observable<boolean> {
    service['@class'] = 'de.cinovo.cloudconductor.api.model.Service';
    return this.http.put<boolean>(this._basePathURL, service);
  }

}
