import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import { HTTPService } from './abstract.http.service';

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

@Injectable()
export class PackageHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'package/';
  }

  public getPackages(): Observable<Array<Package>> {
    return this._get('', null);
  }

  public getPackagesPagewise(page = 0, pageSize = 0): Observable<Response> {
    return this._getResponse('', null, {page: page, per_page: pageSize});
  }

  public getPackage(pkgName: string): Observable<Package> {
    return this._get(pkgName);
  }

  public getVersions(pkg: Package): Observable<Array<PackageVersion>> {
    return this._get(pkg.name + '/versions');
  }

  public getUsage(pkg: Package): Observable<any> {
    return this._get(pkg.name + '/usage');
  }

  public getVersionsOfRepo(repoName: string): Observable<Array<PackageVersion>> {
    return this._get('versions/repo/' + repoName);
  }

}
