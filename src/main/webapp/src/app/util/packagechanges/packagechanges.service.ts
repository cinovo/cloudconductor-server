import { Sorter } from '../sorters.util';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Host } from "../http/host.http.service";
import { PackageHttpService, PackageStateChanges } from "../http/package.http.service";

export interface PackageChange {
  name: string;
  hostVersion: string;
  templateVersion: string;
  state: string
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class PackageChangesService {

  constructor(private packageHttp: PackageHttpService) {
  }

  public computePackageChanges(host: Host): Observable<PackageChange[]> {
    return this.packageHttp.getPackageChanges(host.name).flatMap((psc: PackageStateChanges) => {
      const packageChanges: PackageChange[] = [];
      for (let pkg of psc.toInstall) {
        packageChanges.push({
          name: pkg.name,
          hostVersion: host.packages[pkg.name],
          templateVersion: pkg.version,
          state: 'installing'
        });
      }
      for (let pkg of psc.toUpdate) {
        let comp = Sorter.versionComp(host.packages[pkg.name], pkg.version);
        packageChanges.push({
          name: pkg.name,
          hostVersion: host.packages[pkg.name],
          templateVersion: pkg.version,
          state: comp < 0 ? 'upgrading' : 'downgrading'
        });
      }

      for (let pkg of psc.toErase) {
        packageChanges.push({
          name: pkg.name,
          hostVersion: host.packages[pkg.name],
          templateVersion: pkg.version,
          state: 'uninstalling'
        });
      }
      packageChanges.sort(Sorter.nameField);
      return Observable.of(packageChanges)
    });
  }
}
