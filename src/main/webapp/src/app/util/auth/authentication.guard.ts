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
  return (_route, state) => {
    const router = inject(Router);

    const redirect = state.url;
    return inject(AuthTokenProviderService).loggedIn.pipe(
        map(actualLoggedIn => {
          if (actualLoggedIn === expectedLoggedIn) {
            return true;
          }
          if (!actualLoggedIn) {
            return router.createUrlTree(['/login'], {queryParams: {redirect}});
          }
          if (redirect !== "/login") {
            return router.createUrlTree([redirect]);
          }
          return router.createUrlTree(["/home"]);
        })
    );
  }
}
