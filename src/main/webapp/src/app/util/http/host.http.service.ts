import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { share } from 'rxjs/operators';

import { ServiceState } from '../enums.util';

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

  private readonly _basePathURL = 'api/host';

  constructor(private readonly http: HttpClient) { }

  public getHosts(): Observable<Host[]> {
    return this.http.get<Host[]>(this._basePathURL).pipe(share());
  }

  public getSimpleHosts(): Observable<Host[]> {
    return this.http.get<Host[]>(`${this._basePathURL}/simple`).pipe(share());
  }

  public getHost(hostUuid: string): Observable<Host> {
    return this.http.get<Host>(`${this._basePathURL}/${hostUuid}`).pipe(share());
  }

  public deleteHost(host: Host): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${host.uuid}`).pipe(share());
  }

  public startService(hostUuid: string, service: string): Observable<boolean> {
    const val: ChangeServiceState = {hostUuid, service, targetState: ServiceState.STARTING};
    val['@class'] = 'de.cinovo.cloudconductor.api.model.ChangeServiceState';
    return this.http.put<boolean>(`${this._basePathURL}/changeservicestate`, val).pipe(share());
  }

  public stopService(hostUuid: string, service: string): Observable<boolean> {
    const val: ChangeServiceState = {hostUuid, service, targetState: ServiceState.STOPPING};
    val['@class'] = 'de.cinovo.cloudconductor.api.model.ChangeServiceState';
    return this.http.put<boolean>(`${this._basePathURL}/changeservicestate`, val).pipe(share());
  }

  public restartService(hostUuid: string, service: string): Observable<boolean> {
    const val: ChangeServiceState = {hostUuid, service, targetState: ServiceState.RESTARTING_STOPPING};
    val['@class'] = 'de.cinovo.cloudconductor.api.model.ChangeServiceState';
    return this.http.put<boolean>(`${this._basePathURL}/changeservicestate`, val).pipe(share());
  }

  public moveHost(hostUuid: string, template: string): Observable<boolean>{
    return this.http.get<boolean>(`${this._basePathURL}/${hostUuid}/changetemplate/${template}`).pipe(share());
  }
}
