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
export interface Stats {
  numberOfHosts: number,
  numberOfTemplates: number,
  numberOfServices: number,
  numberOfPackages: number
}

@Injectable()
export class StatsHttpService extends HTTPService {

  constructor(protected http: Http,
              protected authService: AuthenticationService) {
    super(http, authService);
    this.basePathURL = 'stats/';
  }

  public getStats(): Observable<Stats> {
    return this._get('');
  }

}
