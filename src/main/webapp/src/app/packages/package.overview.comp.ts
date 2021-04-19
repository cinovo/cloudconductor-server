import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

import { Subscription } from 'rxjs';

import { AlertService } from '../util/alert/alert.service';
import { Sorter } from '../util/sorters.util';
import { Validator } from '../util/validator.util';
import { Package, PackageHttpService } from '../util/http/package.http.service';

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
export class PackageOverview implements OnInit, OnDestroy {

  public limits: number[] = [15, 30, 50, 100];

  public packagesLoaded = false;
  public totalPackageCount = 0;
  public pageCount = 0;
  public start = 0;
  public end = 0;
  public pages: number[] = [];

  private _searchQuery: string = null;
  private _limit = 15;
  private _page = 1;

  private _packages: Package[] = [];

  private _querySub: Subscription;

  private static filterData(pkg: Package, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return pkg.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  constructor(private packageHttp: PackageHttpService,
              private router: Router,
              private route: ActivatedRoute,
              private alertService: AlertService) { };

  ngOnInit(): void {
    this._querySub = this.route.queryParamMap.subscribe(qpm => {
      this._page = +qpm.get('page') || 1;
      this.loadPackages();
    });
  }

  ngOnDestroy(): void {
    if (this._querySub) {
      this._querySub.unsubscribe();
    }
  }

  private loadPackages() {
    if (this._page && this._limit) {
      this.packageHttp.getPackagesPagewise(this._page, this._limit).subscribe(
        (response: HttpResponse<Package[]>) => {
          this.totalPackageCount = +response.headers.get('x-total-count');

          this.pageCount = Math.ceil(this.totalPackageCount / this.limit);
          this.start = ((this._page - 1) * this._limit + 1);
          this.end = Math.min(this._page * this._limit, this.totalPackageCount);

          this.pages = Array.from(Array(this.pageCount), (x, i) => i + 1);

          this.packages = response.body;
          this.packagesLoaded = true;
        }, (err) => {
          this.alertService.danger('Error loading packages!');
          console.error(err);
          this.packagesLoaded = true;
        }
      );
    } else {
      this.packageHttp.getPackages().subscribe(
        (allPkgs) => {
          this.totalPackageCount = allPkgs.length;
          this.packages = allPkgs;
          this.pageCount = 1;
          this.start = 1;
          this.end = allPkgs.length;
          this.pages = [1];
          this.packagesLoaded = true;
        }, (err) => {
          this.alertService.danger('Error loading all packages!');
          console.error(err);
          this.packagesLoaded = true;
        }
      )
    }
  }

  get packages(): Package[] {
    return this._packages;
  }

  set packages(value: Package[]) {
    this._packages = value
      .map(p => {
        p.versions = p.versions.slice().sort(Sorter.versionComp);
        return p;
      })
      .filter(pkg => PackageOverview.filterData(pkg, this._searchQuery))
      .sort(Sorter.packages);
  }

  get limit(): number {
    return this._limit;
  }

  set limit(value: number) {
    this._limit = value;
    this.router.navigate(['/package'], { queryParams: { page: 1 }});
    this.loadPackages();
  }

  get page(): number {
    return this._page;
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value.trim();
    if (this._searchQuery.length > 0) {
      this._page = 0;
      this._limit = 0;
    } else {
      this._page = 1;
      this._limit = 15
    }
    this.loadPackages();
  }

  public gotoDetails(pkg: Package): void {
    if (Validator.notEmpty(pkg.name)) {
      this.router.navigate(['/package', pkg.name]);
    }
  }

}
