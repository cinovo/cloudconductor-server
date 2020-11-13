
import {map} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';

import { Observable } from 'rxjs';

import { AuthTokenProviderService } from './authtokenprovider.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class AuthenticationGuard implements CanActivate {

  constructor(private authTokenProvider: AuthTokenProviderService,
              private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    let expectedLoggedIn = true;
    if (route.data.loggedIn === false) {
      expectedLoggedIn = false;
    }

    const requestedRoute = route.url;

    return this.authTokenProvider.loggedIn.pipe(map(actualLoggedIn => {
      const match = (actualLoggedIn === expectedLoggedIn);
      if (!match) {
        if (actualLoggedIn) {
          this.router.navigate(['/home']);
        } else {
          this.router.navigate(['/login'], { queryParams: { redirect: requestedRoute}});
        }
      }
      return match;
    }));
  }
}
