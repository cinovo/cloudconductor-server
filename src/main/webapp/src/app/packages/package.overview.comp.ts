import { Component, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';

import { Service, ServiceHttpService } from '../util/http/service.http.service';
import { Sorter } from '../util/sorters.util';
import { Validator } from '../util/validator.util';
import { PackageHttpService, Package } from '../util/http/package.http.service';
/**
  * Copyright 2017 Cinovo AG<br>
  * <br>
  *
  * @author psigloch
  */
@Component({
  selector: 'package-overview',
  templateUrl: './package.overview.comp.html'
})
export class PackageOverview implements AfterViewInit {

  private _searchQuery: string = null;

  private _packages: Array<Package> = [];

  private static filterData(pkg: Package, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return pkg.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  constructor(private packageHttp: PackageHttpService, private router: Router) {
  };

  ngAfterViewInit(): void {
    this.loadPackages();
  }

  private loadPackages() {
    this.packageHttp.getPackages().subscribe(
      (result) => this.packages = result
    )
  }

  get packages(): Array<Package> {
    return this._packages;
  }

  set packages(value: Array<Package>) {
    this._packages = value
      .filter(pkg => PackageOverview.filterData(pkg, this._searchQuery))
      .sort(Sorter.packages);

    for (let pkg of this._packages) {
      pkg.versions.sort((a, b) => Sorter.versionComp(a, b) * -1);
    }
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadPackages();
  }

  protected gotoDetails(pkg: Package) {
    if (Validator.notEmpty(pkg.name)) {
      this.router.navigate(['package', pkg.name]);
    }
  }

}
