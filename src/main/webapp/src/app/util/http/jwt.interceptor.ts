import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';

import { Observable } from 'rxjs';

import { AuthTokenProviderService } from '../auth/authtokenprovider.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private authTokenProvider: AuthTokenProviderService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const requestedUrl = req.urlWithParams;
    if (requestedUrl.match(/^api\/.*/) && req.urlWithParams !== 'api/auth') {
      const clone = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${this.authTokenProvider.token}`)
      });
      return next.handle(clone);
    }

    return next.handle(req);
  }

}
