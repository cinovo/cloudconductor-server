import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { of as observableOf, Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { SSHKey } from './sshkey.model';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class SSHKeyHttpService {

  private readonly _basePathURL = 'api/ssh';

  constructor(private readonly http: HttpClient) { };

  public getKeys(): Observable<SSHKey[]> {
    return this.http.get<SSHKey[]>(this._basePathURL);
  }

  public getKey(owner: string): Observable<SSHKey> {
    return this.http.get<SSHKey>(`${this._basePathURL}/${owner}`).pipe(map((k) => {
      delete k['@class'];
      delete k['lastChanged'];
      return k;
    }));
  }

  public existsKey(owner: string): Observable<boolean> {
    return this.http.get<SSHKey>(`${this._basePathURL}/${owner}`).pipe(
      map(k => (k !== undefined)),
      catchError(() => observableOf(false)),
    );
  }

  public updateKey(sshKey: SSHKey): Observable<boolean> {
    sshKey['@class'] = 'de.cinovo.cloudconductor.api.model.SSHKey';
    return this.http.put<boolean>(this._basePathURL, sshKey);
  }

  public deleteKey(owner: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${owner}`);
  }

}
