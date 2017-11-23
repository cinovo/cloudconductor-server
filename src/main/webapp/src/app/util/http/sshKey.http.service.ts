import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import { AuthenticationService } from '../auth/authentication.service';
import { HTTPService } from './abstract.http.service';
import { SSHKey } from './sshkey.model';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class SSHKeyHttpService extends HTTPService {

  constructor(protected http: Http,
              protected authService: AuthenticationService) {
    super(http, authService);
    this.basePathURL = 'ssh/';
  };

  public getKeys(): Observable<SSHKey[]> {
    return this._get('');
  }

  public getKey(owner: string): Observable<SSHKey> {
    return this._get(owner).map((k) => {
      delete k['@class'];
      delete k['lastChanged'];
      return k;
    });
  }

  public updateKey(sshKey: SSHKey): Observable<boolean> {
    sshKey['@class'] = 'de.cinovo.cloudconductor.api.model.SSHKey';
    return this._put('', sshKey);
  }

  public deleteKey(owner: string): Observable<boolean> {
    return this._delete(owner);
  }

  public existsKey(owner: string): Observable<boolean> {
    return this._get(owner).map((sshKey: SSHKey) => {
      return (sshKey !== undefined);
    }).catch(() => {
      return Observable.of(false);
    });
  }

}
