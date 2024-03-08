import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { map } from 'rxjs/operators';

import { AuthTokenProviderService } from './authtokenprovider.service';
import { Role } from "../enums.util";

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
export const hasRole: (rolesAllowed: Role[]) => CanActivateFn = (rolesAllowed: Role[]) => {
  return () => {
      const router = inject(Router);
      return inject(AuthTokenProviderService).hasSomeRole(rolesAllowed).pipe(
          map(authorized => {
              if (authorized) {
                  return true;
              }
              return router.createUrlTree(['/forbidden']);
          })
      );
  };
}
