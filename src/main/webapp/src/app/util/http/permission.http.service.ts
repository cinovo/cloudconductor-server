import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class PermissionHttpService {

  private _basePath = 'api/permission';

  constructor(private http: HttpClient) { }

  public getPermissions(): Observable<string[]> {
    return this.http.get<string[]>(this._basePath);
  }

}
