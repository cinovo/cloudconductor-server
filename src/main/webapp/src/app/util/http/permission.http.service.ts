import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class PermissionHttpService {

  private readonly _basePath = 'api/permission';

  constructor(private readonly http: HttpClient) { }

  public getPermissions(): Observable<string[]> {
    return this.http.get<string[]>(this._basePath);
  }

}
