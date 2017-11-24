import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { AuthenticationService } from './authentication.service';

@Injectable()
export class AuthenticationGuard implements CanActivate {

  constructor(private authService: AuthenticationService,
              private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    let expectedLoggedIn = true;
    if (route.data.loggedIn === false) {
      expectedLoggedIn = false;
    }

    return this.authService.loggedIn.map(actualLoggedIn => {
      const match = (actualLoggedIn === expectedLoggedIn);
      if (!match) {
        if (actualLoggedIn) {
          this.router.navigate(['/home']);
        } else {
          this.router.navigate(['/login']);
        }
      }
      return match;
    });
  }
}
