import { Subject ,  ReplaySubject } from 'rxjs';

import { JwtHelperService } from '@auth0/angular-jwt';

import { AuthenticatedUser, JwtClaimSet } from '../http/auth.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
export class AuthTokenProviderService {

  public static readonly ANONYMOUS: AuthenticatedUser = {name: '', preferred_username: 'ANONYMOUS', roles: []};

  public loggedIn: Subject<boolean> = new ReplaySubject(1);
  public currentUser: Subject<AuthenticatedUser> = new ReplaySubject(1);

  private _token: string;
  private _nextRefresh: number

  private jwtHelper: JwtHelperService;

  private static getUserFromJwt(jwt: JwtClaimSet): AuthenticatedUser {
    const user: AuthenticatedUser = {
      name: jwt.name,
      preferred_username: jwt.preferred_username,
      roles: jwt.roles
    }
    return user;
  }

  public static isAnonymous(user: AuthenticatedUser): boolean {
    return (user.preferred_username === AuthTokenProviderService.ANONYMOUS.preferred_username);
  }

  constructor() {
    this.jwtHelper = new JwtHelperService();

    const savedToken = localStorage.getItem('token');
    if (savedToken) {
      this.storeToken(savedToken);
    } else {
      this.loggedIn.next(false);
    }

    this._nextRefresh = +localStorage.getItem('refresh');
  }

  get token() {
    return this._token;
  }

  get nextRefresh() {
    return this._nextRefresh;
  }

  set nextRefresh(value: number) {
    this._nextRefresh = value;
    localStorage.setItem('refresh', value.toString());
  }

  public storeToken(value: string): AuthenticatedUser {
    if (!value || this.jwtHelper.isTokenExpired(value)) {
      this.loggedIn.next(false);
      const user = AuthTokenProviderService.ANONYMOUS;
      this.currentUser.next(user);
      return user;
    }

    this.nextRefresh = Math.max(this.jwtHelper.getTokenExpirationDate(value).getTime() - 10000, new Date().getTime());

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
