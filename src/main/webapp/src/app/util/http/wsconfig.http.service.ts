import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

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

  private readonly _basePathURL = 'api/wsconfig';

  constructor(private readonly http: HttpClient) { }

  public getWSConfig(): Observable<WSConfig> {
    return this.http.get<WSConfig>(this._basePathURL);
  }

}
