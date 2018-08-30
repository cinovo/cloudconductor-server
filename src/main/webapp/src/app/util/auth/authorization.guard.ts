import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { AuthTokenProviderService } from './authtokenprovider.service';
import { Role } from "../enums.util";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class AuthorizationGuard implements CanActivate {

  constructor(private authTokenProvider: AuthTokenProviderService,
              private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    if (route.data && route.data.rolesAllowed && route.data.rolesAllowed.length > 0) {
      const rolesAllowed: string[] = route.data.rolesAllowed;

      return this.authTokenProvider.currentUser
        .map(user => user.roles.filter(r => rolesAllowed.includes(r)))
        .map(matchingRoles => {
          const allowed = (matchingRoles.length > 0);
          if (!allowed) {
            this.router.navigate(['/forbidden']);
          }
          return allowed;
        });
    }

    return Observable.of(true);
  }

  hasRole(role: Role): Observable<boolean> {
    return this.authTokenProvider.currentUser
      .map(user => user.roles.filter(r => r == role))
      .map(matchingRoles => {
        return matchingRoles.length > 0
      });
  }

}
