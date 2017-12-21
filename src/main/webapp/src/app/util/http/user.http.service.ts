import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
export interface User {
  loginName: string,
  displayName: string,
  registrationDate?: Date,
  active?: boolean,
  email: string,
  password?: string,
  userGroups?: string[],
  agents?: string[],
  authTokens?: AuthToken[]
}

export interface AuthToken {
  token: string,
  creationDate: Date,
  revoked: Date
}

export interface PasswordChangeRequest {
  oldPassword: string,
  newPassword: string,
  userName: string
}

@Injectable()
export class UserHttpService {

  private static readonly emptyUser: User = {
    loginName: '', displayName: '', email: '', registrationDate: new Date(), active: true,
    userGroups: [], agents: [], authTokens: []
  };

  private _basePath = 'api/user';

  public static getEmptyUser(): User {
    return Object.assign({}, UserHttpService.emptyUser);
  }

  constructor(private http: HttpClient) { }

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this._basePath)
  }

  public getUser(username: string): Observable<User> {
    return this.http.get<User>(`${this._basePath}/${username}`)
                              .map(u => Object.assign(UserHttpService.getEmptyUser(), u));
  }

  public existsUser(username: string): Observable<boolean> {
    return this.getUser(username)
              .map((u) => u.loginName && u.loginName.length > 0)
              .catch(err => Observable.of(false));
  }

  public saveUser(userToSave: User): Observable<boolean> {
    userToSave['@class'] = 'de.cinovo.cloudconductor.api.model.User';
    return this.http.put<boolean>(this._basePath, userToSave);
  }

  public deleteUser(username: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePath}/${username}`);
  }

  public createAuthToken(username: string): Observable<User> {
    return this.http.put<User>(`${this._basePath}/${username}/authtoken`, {});
  }

  public revokeAuthToken(username: string, authToken: AuthToken): Observable<User> {
    return this.http.delete<User>(`${this._basePath}/${username}/authtoken/${authToken.token}`);
  }

  public changePassword(passwordChange: PasswordChangeRequest): Observable<boolean> {
    return this.http.put<boolean>(`${this._basePath}/changepassword`, passwordChange);
  }

}
