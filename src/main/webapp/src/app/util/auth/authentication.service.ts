import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { ReplaySubject } from 'rxjs/ReplaySubject';

import { HTTPService } from '../http/abstract.http.service';

export interface Authentication {
  username: string,
  password: string,
  token: string
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class AuthenticationService {

  public token = '';
  public loggedIn: Subject<boolean> = new ReplaySubject(1);

  protected headers = new Headers(
    {'Content-Type': 'application/json'}
  );

  constructor(protected http: Http) {
    const savedToken = localStorage.getItem('token');
    if (savedToken) {
      this.token = savedToken;
      // TODO check if token is still valid
      this.loggedIn.next(true);
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
        localStorage.setItem('token', jwt);
        this.loggedIn.next(true);
        return true;
      }
      return false;
    }).share();
  }

  public logout() {
    // TODO first do read logout
    this.token = '';
    localStorage.removeItem('token');
    this.loggedIn.next(false);
  }

}
