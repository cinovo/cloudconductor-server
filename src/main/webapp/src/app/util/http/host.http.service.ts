import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

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
  uuid: string;
  description?: string;
  lastSeen?: number;
  services?: { [serviceName: string]: ServiceState };
  numberOfServices?: number;
  packages?: { [pkgName: string]: string };
  numberOfPackages?: number;
}


export interface ChangeServiceState {
  hostUuid: string;
  service: string;
  targetState: ServiceState;
}

@Injectable()
export class HostHttpService {

  private _basePathURL = 'api/host';

  constructor(private http: HttpClient) {
  }

  public getHosts(): Observable<Host[]> {
    return this.http.get<Host[]>(this._basePathURL).share();
  }

  public getSimpleHosts(): Observable<Host[]> {
    return this.http.get<Host[]>(`${this._basePathURL}/simple`).share();
  }

  public getHost(hostUuid: string): Observable<Host> {
    return this.http.get<Host>(`${this._basePathURL}/${hostUuid}`).share();
  }

  public deleteHost(host: Host): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${host.uuid}`).share();
  }

  public startService(hostUuid: string, serviceName: string): Observable<boolean> {
    let val: ChangeServiceState = {hostUuid: hostUuid, service: serviceName, targetState: ServiceState.STARTING};
    val['@class'] = 'de.cinovo.cloudconductor.api.model.ChangeServiceState';
    return this.http.put<boolean>(`${this._basePathURL}/changeservicestate`, val).share();
  }

  public stopService(hostUuid: string, serviceName: string): Observable<boolean> {
    let val: ChangeServiceState = {hostUuid: hostUuid, service: serviceName, targetState: ServiceState.STOPPING};
    val['@class'] = 'de.cinovo.cloudconductor.api.model.ChangeServiceState';
    return this.http.put<boolean>(`${this._basePathURL}/changeservicestate`, val).share();
  }

  public restartService(hostUuid: string, serviceName: string): Observable<boolean> {
    let val: ChangeServiceState = {hostUuid: hostUuid, service: serviceName, targetState: ServiceState.RESTARTING_STOPPING};
    val['@class'] = 'de.cinovo.cloudconductor.api.model.ChangeServiceState';
    return this.http.put<boolean>(`${this._basePathURL}/changeservicestate`, val).share();
  }
}
