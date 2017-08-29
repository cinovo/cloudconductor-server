import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs';

import { HTTPService } from './abstract.http.service';

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
  packages?: Array<String>;
  templates?: Array<String>;
}

@Injectable()
export class ServiceHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'service/';
  }

  public getServices(): Observable<Array<Service>> {
    return this._get('');
  }

  public getService(serviceName: string): Observable<Service> {
    return this._get(serviceName);
  }


  public getServiceUsage(serviceName: string): Observable<any> {
    return this._get(serviceName + '/usage');
  }

  public deleteService(service: Service): Observable<boolean> {
    return this._delete(service.name);
  }

  public save(service: Service): Observable<boolean> {
    service['@class'] = 'de.cinovo.cloudconductor.api.model.Service';
    return this._put('', service);
  }

  public getServiceNames(): Observable<string[]> {
    return this.getServices().map((services: Service[]) => services.map(s => s.name));
  }

}
