import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

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
export class WSConfigHttpService {

  private _basePathURL = 'api/wsconfig';

  constructor(private http: HttpClient) { }

  public getWSConfig(): Observable<WSConfig> {
    return this.http.get<WSConfig>(this._basePathURL);
  }

}
