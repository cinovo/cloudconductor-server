import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { AuthenticationService } from './authentication.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthenticationService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    if (route.data && route.data.rolesAllowed && route.data.rolesAllowed.length > 0) {
      const rolesAllowed: string[] = route.data.rolesAllowed;

      return this.authService.currentUser.map((user) => {
        return user.roles.filter(r => rolesAllowed.includes(r)).length > 0;
      });
    }
    return Observable.of(true);
  }

}
