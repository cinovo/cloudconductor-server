import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { map } from 'rxjs/operators';

import { AuthTokenProviderService } from './authtokenprovider.service';

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
export const loggedIn: (expectedLoggedIn: boolean) => CanActivateFn = (expectedLoggedIn ) => {
  return (route) => {
    const router = inject(Router);

    const redirect = route.url;
    return inject(AuthTokenProviderService).loggedIn.pipe(
        map(actualLoggedIn => {
          if (actualLoggedIn === expectedLoggedIn) {
            return true;
          }
          return router.createUrlTree(['/login'], {queryParams: {redirect}});
        })
    );
  }
}
