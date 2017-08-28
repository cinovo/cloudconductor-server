import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs/';

import { HTTPService } from './abstract.http.service';

export interface SSHKey {
  owner: string,
  username: string,
  key: string,
  lastChanged?: Date
  templates: string[]
}

@Injectable()
export class SSHKeyHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'ssh/';
  };

  public getKeys(): Observable<SSHKey[]> {
    return this._get('');
  }

  public getKey(owner: string): Observable<SSHKey> {
    return this._get(owner);
  }

  public updateKey(sshKey: SSHKey): Observable<boolean> {
    sshKey['@class'] = 'de.cinovo.cloudconductor.api.model.SSHKey';
    return this._put('', sshKey);
  }

  public deleteKey(owner: string): Observable<boolean> {
    return this._delete(owner);
  }

}
