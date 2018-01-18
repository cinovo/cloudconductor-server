import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface Package {
  name: string;
  description?: string;
  versions: Array<string>;
}

export interface PackageVersion {
  name: string;
  version: string;
  dependencies: Array<Dependency>;
  repos: Array<string>;
}

export interface Dependency {
  name: string;
  version: string;
  operator: string;
  type: string;
}

export interface PackageStateChanges {
  toInstall: Array<PackageVersion>;
  toUpdate: Array<PackageVersion>;
  toErase: Array<PackageVersion>;
}

@Injectable()
export class PackageHttpService {

  private _basePathURL = 'api/package';

  constructor(private http: HttpClient) {
  }

  public getPackages(): Observable<Package[]> {
    return this.http.get<Package[]>(this._basePathURL);
  }

  public getPackagesPagewise(page = 0, pageSize = 0): Observable<HttpResponse<Package[]>> {
    const params = new HttpParams().set('page', page.toString())
      .set('per_page', pageSize.toString());
    return this.http.get<Package[]>(this._basePathURL, {observe: 'response', params: params});
  }

  public getPackage(pkgName: string): Observable<Package> {
    return this.http.get<Package>(`${this._basePathURL}/${pkgName}`);
  }

  public getVersions(pkg: Package): Observable<PackageVersion[]> {
    return this.http.get<PackageVersion[]>(`${this._basePathURL}/${pkg.name}/versions`);
  }

  public getUsage(pkg: Package): Observable<any> {
    return this.http.get(`${this._basePathURL}/${pkg.name}/usage`);
  }

  public getVersionsOfRepo(repoName: string): Observable<PackageVersion[]> {
    return this.http.get<PackageVersion[]>(`${this._basePathURL}/versions/repo/${repoName}`);
  }

  public getPackageChanges(hostName: string): Observable<PackageStateChanges> {
    return this.http.get<PackageStateChanges>(`${this._basePathURL}/changes/${hostName}`);
  }
}
