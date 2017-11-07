import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

import { Observable } from 'rxjs';

import { HTTPService } from './abstract.http.service';
import { ServiceState } from '../../util/enums.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface Host {
  name: string;
  template: string;
  agent?: string;
  description?: string;
  lastSeen?: number;
  services?: {[serviceName: string]: ServiceState};
  packages?: {[pkgName: string]: string};
}

@Injectable()
export class HostHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'host/';
  }

  public getHosts(): Observable<Array<Host>> {
    return this._get('');
  }

  public getHost(hostName: string): Observable<Host> {
    return this._get(hostName);
  }

  public deleteHost(host: Host): Observable<boolean> {
    return this._delete(host.name);
  }

  public startService(hostName: string, serviceName: string): Observable<boolean> {
    return this._put(hostName + '/' + serviceName, ServiceState.STARTING);
  }

  public stopService(hostName: string, serviceName: string): Observable<boolean> {
    return this._put(hostName + '/' + serviceName, ServiceState.STOPPING);
  }

  public restartService(hostName: string, serviceName: string): Observable<boolean> {
    return this._put(hostName + '/' + serviceName, ServiceState.RESTARTING_STOPPING);
  }
}
