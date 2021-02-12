
import {of as observableOf,  Observable } from 'rxjs';

import {map} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';

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

  constructor(private readonly authTokenProvider: AuthTokenProviderService,
              private readonly router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    if (route.data && route.data.rolesAllowed && route.data.rolesAllowed.length > 0) {
      const rolesAllowed: string[] = route.data.rolesAllowed;

      return this.authTokenProvider.currentUser.pipe(
        map(user => user.roles.filter(r => rolesAllowed.includes(r))),
        map(matchingRoles => {
          const allowed = (matchingRoles.length > 0);
          if (!allowed) {
            this.router.navigate(['/forbidden']);
          }
          return allowed;
        })
      );
    }

    return observableOf(true);
  }

  hasRole(role: Role): Observable<boolean> {
    return this.authTokenProvider.currentUser.pipe(
      map(user => user.roles.filter(r => r == role)),
      map(matchingRoles => matchingRoles.length > 0)
    );
  }

}
