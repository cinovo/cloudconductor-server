import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { of as observableOf, Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { RepoMirror } from './repomirror.http.service';

export interface Repo {
  id?: number;
  name: string;
  description?: string
  mirrors: Array<RepoMirror>;
  primaryMirror?: number;
  lastIndex?: number;
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Injectable()
export class RepoHttpService {

  private readonly _basePathURL = 'api/repo';

  constructor(private readonly http: HttpClient) { }

  public getRepos(): Observable<Repo[]> {
    return this.http.get<Repo[]>(this._basePathURL);
  }

  public getRepo(repoName: string): Observable<Repo> {
    return this.http.get<Repo>(`${this._basePathURL}/${repoName}`);
  }

  public existsRepo(repoName: string): Observable<boolean> {
    return this.getRepo(repoName).pipe(
                map(r => r !== undefined && r.id !== undefined),
                catchError(_ => observableOf(false)),
    );
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

  public forceReindex(repo: Repo): Observable<boolean> {
    return this.http.put<boolean>(`${this._basePathURL}/${repo.name}/forceupdate`, {});
  }
}
