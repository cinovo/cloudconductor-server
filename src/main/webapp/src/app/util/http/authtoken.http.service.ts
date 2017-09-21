import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import { HTTPService } from './abstract.http.service';

export interface AuthToken {
  token: string,
  creationDate: Date,
  revoked: Date,
  revokeComment?: string
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class AuthTokenHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'authtoken/'
  }

  public getAuthTokens(): Observable<AuthToken[]> {
    return this._get('');
  }

  public generateToken(): Observable<AuthToken> {
    return this._put('generate', []);
  }

}
