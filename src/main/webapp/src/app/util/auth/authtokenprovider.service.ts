import { Subject } from 'rxjs/Subject';
import { ReplaySubject } from 'rxjs/ReplaySubject';

import { JwtHelper } from 'angular2-jwt';
import { User, JwtClaimSet } from '../http/auth.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
export class AuthTokenProviderService {

  public static readonly ANONYMOUS: User = {name: 'ANONYMOUS', preferred_username: '', roles: []};

  public loggedIn: Subject<boolean> = new ReplaySubject(1);
  public currentUser: Subject<User> = new ReplaySubject(1);

  private _token: string;

  private jwtHelper: JwtHelper;

  private static getUserFromJwt(jwt: JwtClaimSet): User {
    const user: User = {
      name: jwt.name,
      preferred_username: jwt.preferred_username,
      roles: jwt.roles
    }
    return user;
  }

  public static isAnonymous(user: User): boolean {
    return (user.name === AuthTokenProviderService.ANONYMOUS.name);
  }

  constructor() {
    this.jwtHelper = new JwtHelper();

    const savedToken = localStorage.getItem('token');
    if (savedToken) {
      this.storeToken(savedToken);
    } else {
      this.loggedIn.next(false);
    }
  }

  get token() {
    return this._token;
  }

  public storeToken(value: string): User {
    if (!value || this.jwtHelper.isTokenExpired(value)) {
      this.loggedIn.next(false);
      const user =  AuthTokenProviderService.ANONYMOUS
      this.currentUser.next(user)
      return user;
    }

    this._token = value;
    localStorage.setItem('token', value);
    this.loggedIn.next(true);

    const decodedToken: JwtClaimSet = this.jwtHelper.decodeToken(value);
    const currentUser = AuthTokenProviderService.getUserFromJwt(decodedToken);
    this.currentUser.next(currentUser);
    return currentUser;
  }

  public removeToken() {
    this._token = '';
    localStorage.removeItem('token');
    this.loggedIn.next(false);
    this.currentUser.next(AuthTokenProviderService.ANONYMOUS);
  }

}
