
import {throwError as observableThrowError,  Observable } from 'rxjs';

import {catchError} from 'rxjs/operators';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { AuthTokenProviderService } from '../auth/authtokenprovider.service';

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class UnauthorizedInterceptor implements HttpInterceptor {

  constructor(private router: Router,
              private authTokenProvider: AuthTokenProviderService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError(error => {

      // we are only interested in HTTP 401 Unauthorized
      if (error.status === 401) {
        // our JWT is no longer valid, throw it away and show login
        this.authTokenProvider.removeToken();
        this.router.navigate(['/login']);
      }

      // different error occurred, rethrow it
      return observableThrowError(error);
    }));
  }

}
