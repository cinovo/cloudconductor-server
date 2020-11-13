import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Role } from '../enums.util';

export interface JwtClaimSet {
  exp: number,
  iss: string,
  name: string
  preferred_username: string,
  roles: Role[]
  sub: string
}

export interface Authentication {
  username: string,
  password: string,
  token: string
}

export interface AuthenticatedUser {
  name: string,
  preferred_username: string,
  roles: Role[]
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class AuthHttpService {

  private _basePathURL = 'api/auth';

  constructor(private http: HttpClient) { }

  public login(auth: Authentication): Observable<string> {
    return this.http.put<string>(this._basePathURL, auth);
  }

  public refresh(currentToken: string): Observable<string> {
    return this.http.put<string>(`${this._basePathURL}/refresh`, currentToken);
  }

  public logout(): Observable<boolean> {
    return this.http.put<boolean>(`${this._basePathURL}/logout`, {});
  }

}
