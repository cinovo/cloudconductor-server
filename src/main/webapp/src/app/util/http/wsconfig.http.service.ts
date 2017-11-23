import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import { HTTPService } from './abstract.http.service';
import { AuthenticationService } from '../auth/authentication.service';

export interface WSConfig {
  basePath: string,
  timeout: number
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class WSConfigHttpService extends HTTPService {

  constructor(protected http: Http,
              protected authService: AuthenticationService) {
    super(http, authService);
    this.basePathURL = 'wsconfig/';
  }

  public getWSConfig(): Observable<WSConfig> {
    return this._get('');
  }

}
