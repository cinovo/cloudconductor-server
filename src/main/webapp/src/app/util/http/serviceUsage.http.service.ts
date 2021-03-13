import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { ServiceUsage } from './service.http.service';

export type ServiceUsages = Record<string, ServiceUsage>

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class ServiceUsageHttpService {

  private readonly _basePathURL = 'api/serviceusages';

  constructor(private readonly http: HttpClient) { }

  public getUsages(): Observable<ServiceUsages> {
    return this.http.get<ServiceUsages>(this._basePathURL);
  }

}
