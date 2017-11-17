import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import { HTTPService } from './abstract.http.service';

export interface Stats {
  numberOfHosts: number,
  numberOfTemplates: number,
  numberOfServices: number,
  numberOfPackages: number
}

@Injectable()
export class StatsHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'stats/';
  }

  public getStats(): Observable<Stats> {
    return this._get('');
  }

}
