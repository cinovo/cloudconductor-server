import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

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
export class StatsHttpService {

  private _basePathURL = 'api/stats';

  constructor(private http: HttpClient) { }

  public getStats(): Observable<Stats> {
    return this.http.get<Stats>(this._basePathURL);
  }

}
