import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import { AuthenticationService } from '../auth/authentication.service';
import { HTTPService } from './abstract.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class AuthHttpService extends HTTPService {

  constructor(protected http: Http,
              protected authService: AuthenticationService) {
    super(http, authService);
    this.basePathURL = 'auth/';
  }

  public logout(): Observable<boolean> {
    return this._put('logout', {});
  }

}
