import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { ReplaySubject } from 'rxjs/ReplaySubject';

import { JwtHelper } from 'angular2-jwt';

import { AuthHttpService } from '../http/auth.http.service';
import { HTTPService } from '../http/abstract.http.service';

export interface Authentication {
  username: string,
  password: string,
  token: string
}

export interface User {
  name: string,
  preferred_username: string,
  roles: string[]
}

export interface JwtClaimSet {
  exp: number,
  iss: string,
  name: string
  preferred_username: string,
  roles: string[]
  sub: string
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class AuthenticationService {

  private readonly _anonymous_user = {name: 'ANONYMOUS', preferred_username: '', roles: []};

  private _token = '';
  public loggedIn: Subject<boolean> = new ReplaySubject(1);
  public currentUser: Subject<User> = new ReplaySubject(1);

  protected headers = new Headers({'Content-Type': 'application/json'});

  private jwtHelper: JwtHelper;

  private static getUserFromJwt(jwt: JwtClaimSet): User {
    const user: User = {
      name: jwt.name,
      preferred_username: jwt.preferred_username,
      roles: jwt.roles
    }
    return user;
  }

  constructor(protected http: Http) {
    this.jwtHelper = new JwtHelper();

    const savedToken = localStorage.getItem('token');
    if (savedToken) {
      this.token = savedToken;
    }
  }

  get token() {
    return this._token;
  }

  set token(value: string) {
    const expired = this.jwtHelper.isTokenExpired(value);

    if (!expired) {
      this._token = value;
      localStorage.setItem('token', value);
      this.loggedIn.next(true);

      const decodedToken: JwtClaimSet = this.jwtHelper.decodeToken(value);
      this.currentUser.next(AuthenticationService.getUserFromJwt(decodedToken));
    }
  }

  private extractData(res: Response): any {
    try {
      return res.json();
    } catch (error) {
      return res;
    }
  }

  public login(auth: Authentication): Observable<boolean> {
    return this.http
      .put('/api/auth', JSON.stringify(auth), {headers: this.headers})
      .map((response) => {
        let result = this.extractData(response);
        if (result && Object.keys(result).length === 0) {
          return auth;
        }
        return result;
      }).map((jwt) => {
        if (jwt) {
          this.token = jwt;
          return true;
        }
        return false;
      }).share();
  }

  public removeToken() {
    this._token = '';
    localStorage.removeItem('token');
    this.loggedIn.next(false);
    this.currentUser.next(this._anonymous_user);
  }

}
