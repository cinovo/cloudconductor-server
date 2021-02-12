import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

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
export class RepoMirrorHttpService {

  private readonly _basePathURL = 'api/repomirror';

  constructor(private readonly http: HttpClient) { }

  public getMirror(id: string): Observable<RepoMirror> {
    return this.http.get<RepoMirror>(`${this._basePathURL}/${id}`);
  }

  public deleteMirror(id: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${id}`);
  }

  public newMirror(mirror: RepoMirror): Observable<RepoMirror> {
    mirror['@class'] = 'de.cinovo.cloudconductor.api.model.RepoMirror';
    return this.http.post<RepoMirror>(this._basePathURL, mirror);
  }

  public editMirror(mirror: RepoMirror): Observable<RepoMirror> {
    mirror['@class'] = 'de.cinovo.cloudconductor.api.model.RepoMirror';
    return this.http.put<RepoMirror>(this._basePathURL, mirror);
  }
}
