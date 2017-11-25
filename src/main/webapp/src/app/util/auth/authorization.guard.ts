import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { AuthenticationService } from './authentication.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class AuthorizationGuard implements CanActivate {

  constructor(private authService: AuthenticationService,
              private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    if (route.data && route.data.rolesAllowed && route.data.rolesAllowed.length > 0) {
      const rolesAllowed: string[] = route.data.rolesAllowed;

      return this.authService.currentUser.map(user => user.roles.filter(r => rolesAllowed.includes(r)))
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

}
