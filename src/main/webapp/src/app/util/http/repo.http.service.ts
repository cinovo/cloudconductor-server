import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

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
export class RepoHttpService {

  private _basePathURL = 'api/repo';

  constructor(private http: HttpClient) { }

  public getRepos(): Observable<Repo[]> {
    return this.http.get<Repo[]>(this._basePathURL);
  }

  public getRepo(repoName: string): Observable<Repo> {
    return this.http.get<Repo>(`${this._basePathURL}/${repoName}`);
  }

  public existsRepo(repoName: string): Observable<boolean> {
    return this.getRepo(repoName)
                .map(r => r !== undefined && r.id !== undefined)
                .catch(err => Observable.of(false));
  }

  public deleteRepo(repoName: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${repoName}`);
  }

  public newRepo(repo: Repo): Observable<Repo> {
    repo['@class'] = 'de.cinovo.cloudconductor.api.model.Repo';
    return this.http.post<Repo>(this._basePathURL, repo);
  }

  public editRepo(repo: Repo): Observable<Repo> {
    repo['@class'] = 'de.cinovo.cloudconductor.api.model.Repo';
    return this.http.put<Repo>(this._basePathURL, repo);
  }

}
