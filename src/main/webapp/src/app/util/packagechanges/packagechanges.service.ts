import { Injectable } from '@angular/core';

import { of as observableOf, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { Sorter } from '../sorters.util';
import { Host } from "../http/host.http.service";
import { PackageHttpService, PackageStateChanges } from "../http/package.http.service";
import { Validator } from "../validator.util";

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

  constructor(private readonly packageHttp: PackageHttpService) { }

  public computePackageChanges(host: Host): Observable<PackageChange[]> {
    if (!host || !Validator.notEmpty(host.uuid) || !host.packages) {
      const packageChanges: PackageChange[] = [];
      return observableOf(packageChanges);
    }
    return this.packageHttp.getPackageChanges(host.uuid).pipe(mergeMap((psc: PackageStateChanges) => {
      const packageChanges: PackageChange[] = [];
      for (const pkg of psc.toInstall) {
        packageChanges.push({
          name: pkg.name,
          hostVersion: host.packages[pkg.name],
          templateVersion: pkg.version,
          state: 'installing'
        });
      }

      for (const pkg of psc.toUpdate) {
        const comp = Sorter.versionComp(host.packages[pkg.name], pkg.version);
        if (comp === 0) {
          continue;
        }
        packageChanges.push({
          name: pkg.name,
          hostVersion: host.packages[pkg.name],
          templateVersion: pkg.version,
          state: comp < 0 ? 'upgrading' : 'downgrading'
        });
      }

      for (const pkg of psc.toErase) {
        packageChanges.push({
          name: pkg.name,
          hostVersion: host.packages[pkg.name],
          templateVersion: pkg.version,
          state: 'uninstalling'
        });
      }
      packageChanges.sort(Sorter.nameField);
      return observableOf(packageChanges)
    }));
  }
}
