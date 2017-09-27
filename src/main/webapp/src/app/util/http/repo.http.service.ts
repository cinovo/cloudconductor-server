import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs';

import { HTTPService } from './abstract.http.service';
import { RepoMirror } from './repomirror.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface Repo {
  id?: number;
  name: string;
  description?: string
  mirrors: Array<RepoMirror>;
  primaryMirror?: number;
  lastIndex?: number;
}

@Injectable()
export class RepoHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'repo/';
  }

  public getRepos(): Observable<Array<Repo>> {
    return this._get('');
  }

  public getRepo(repoName: string): Observable<Repo> {
    return this._get(repoName);
  }

  public deleteRepo(repoName: string): Observable<boolean> {
    return this._delete(repoName);
  }

  public newRepo(repo: Repo): Observable<Repo> {
    repo['@class'] = 'de.cinovo.cloudconductor.api.model.Repo';
    return this._post('', repo);
  }

  public editRepo(repo: Repo): Observable<Repo> {
    repo['@class'] = 'de.cinovo.cloudconductor.api.model.Repo';
    return this._put('', repo);
  }

}
