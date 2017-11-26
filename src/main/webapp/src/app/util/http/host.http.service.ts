import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

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
  numberOfServices?: number;
  packages?: {[pkgName: string]: string};
  numberOfPackages?: number;
}

@Injectable()
export class HostHttpService {

  private _basePathURL = 'api/host';

  constructor(private http: HttpClient) { }

  public getHosts(): Observable<Host[]> {
    return this.http.get<Host[]>(this._basePathURL).share();
  }

  public getHost(hostName: string): Observable<Host> {
    return this.http.get<Host>(`${this._basePathURL}/${hostName}`).share();
  }

  public deleteHost(host: Host): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${host.name}`).share();
  }

  public startService(hostName: string, serviceName: string): Observable<boolean> {
    return this.http.put<boolean>(`${this._basePathURL}/${hostName}/${serviceName}`, ServiceState.STARTING).share();
  }

  public stopService(hostName: string, serviceName: string): Observable<boolean> {
    return this.http.put<boolean>(`${this._basePathURL}/${hostName}/${serviceName}`, ServiceState.STOPPING).share();
  }

  public restartService(hostName: string, serviceName: string): Observable<boolean> {
    return this.http.put<boolean>(`${this._basePathURL}/${hostName}/${serviceName}`, ServiceState.RESTARTING_STOPPING).share();
  }
}
