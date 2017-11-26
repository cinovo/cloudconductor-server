import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

import { SSHKey } from './sshkey.model';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class SSHKeyHttpService {

  private _basePathURL = 'api/ssh';

  constructor(private http: HttpClient) { };

  public getKeys(): Observable<SSHKey[]> {
    return this.http.get<SSHKey[]>(this._basePathURL);
  }

  public getKey(owner: string): Observable<SSHKey> {
    return this.http.get<SSHKey>(`${this._basePathURL}/${owner}`).map((k) => {
      delete k['@class'];
      delete k['lastChanged'];
      return k;
    });
  }

  public existsKey(owner: string): Observable<boolean> {
    return this.http.get(`${this._basePathURL}/${owner}`).map((sshKey: SSHKey) => {
      return (sshKey !== undefined);
    }).catch(() => {
      return Observable.of(false);
    });
  }

  public updateKey(sshKey: SSHKey): Observable<boolean> {
    sshKey['@class'] = 'de.cinovo.cloudconductor.api.model.SSHKey';
    return this.http.put<boolean>(this._basePathURL, sshKey);
  }

  public deleteKey(owner: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${owner}`);
  }

}
