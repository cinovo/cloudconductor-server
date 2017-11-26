import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

export interface AuthToken {
  id: number,
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
export class AuthTokenHttpService {

  private _basePathURL = 'api/authtoken'

  constructor(protected http: HttpClient) { }

  public getAuthTokens(): Observable<AuthToken[]> {
    return this.http.get<AuthToken[]>(this._basePathURL);
  }

  public generateToken(): Observable<AuthToken> {
    return this.http.put<AuthToken>(`${this._basePathURL}/generate`, []);
  }

  public revokeToken(tokenId: number, comment: string): Observable<boolean> {
    return this.http.put<boolean>(`${tokenId}/revoke`, comment);
  }

}
