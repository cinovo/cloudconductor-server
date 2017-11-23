import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import { AuthenticationService } from '../auth/authentication.service';
import { HTTPService } from './abstract.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface RepoMirror {
  id?: number;
  repo: string;
  path: string;
  description: string;
  indexerType?: string;
  providerType?: string;
  basePath?: string;
  bucketName?: string;
  awsRegion?: string;
  accessKeyId?: string;
  secretKey?: string;
}

@Injectable()
export class RepoMirrorHttpService extends HTTPService {

  constructor(protected http: Http,
              protected authService: AuthenticationService) {
    super(http, authService);
    this.basePathURL = 'repomirror/';
  }

  public getMirror(id: string): Observable<RepoMirror> {
    return this._get(id);
  }

  public deleteMirror(id: string): Observable<boolean> {
    return this._delete(id);
  }

  public newMirror(mirror: RepoMirror): Observable<RepoMirror> {
    mirror['@class'] = 'de.cinovo.cloudconductor.api.model.RepoMirror';
    return this._post('', mirror);
  }

  public editMirror(mirror: RepoMirror): Observable<RepoMirror> {
    mirror['@class'] = 'de.cinovo.cloudconductor.api.model.RepoMirror';
    return this._put('', mirror);
  }
}
