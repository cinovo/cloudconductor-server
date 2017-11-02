import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Subject } from 'rxjs/Subject';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { Service, ServiceHttpService } from '../util/http/service.http.service';
import { Sorter } from '../util/sorters.util';
import { Validator } from '../util/validator.util';
import { PackageHttpService, Package } from '../util/http/package.http.service';
import { WebSocketService, Heartbeat } from '../util/websockets/websocket.service';

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
export class PackageOverview implements OnInit {

  public packagesLoaded = false;

  private _searchQuery: string = null;

  private _packages: Array<Package> = [];

  private _webSocket: Subject<MessageEvent | Heartbeat>;

  private _webSocketSub: Subscription;

  private static filterData(pkg: Package, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return pkg.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  constructor(private packageHttp: PackageHttpService,
              private router: Router,
              private wsService: WebSocketService,
              private alertService: AlertService) { };

  ngOnInit(): void {
    this.loadPackages();
  }

  private loadPackages() {
    this.packageHttp.getPackages().subscribe((result) => {
      this.packages = result;
      this.packagesLoaded = true;
    }, (err) => {
      this.alertService.danger('Error loading packages!');
      this.packagesLoaded = true;
    });
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
