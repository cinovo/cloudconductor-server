import { Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs/Rx';
import { PackageChange, PackageChangesService } from '../util/packagechanges/packagechanges.service';
import { Component, OnDestroy, OnInit } from '@angular/core';

import { SettingHttpService } from '../util/http/setting.http.service';
import { HostHttpService, Host } from '../util/http/host.http.service';
import { TemplateHttpService } from '../util/http/template.http.service';

interface PackageChangeMap { [key: string]: PackageChange[] };

@Component({
  selector: 'home-packagechanges',
  templateUrl: 'home.pkgchanges.comp.html'
})
export class HomePackageChangesComponent implements OnInit, OnDestroy {

  public packageChanges: PackageChangeMap;

  private hostsSub: Subscription;

  constructor(private hostHttpService: HostHttpService,
              private packageChangesService: PackageChangesService,
              private router: Router) { }

    public ngOnInit(): void {
      // TODO update periodically
      this.hostsSub = this.hostHttpService.getHosts().subscribe((hosts) => {
        this.loadChangesForHosts(hosts);
      });
    }

    private loadChangesForHosts(hosts: Host[]): void {
      const changes$: Observable<PackageChangeMap>[] = hosts.map(host => {
        return this.packageChangesService.computePackageChanges(host).map(changes => {
          let container = {};
          changes = changes.filter(change => change.state !== 'ok' && change.state !== 'protected');
          if (changes.length > 0) {
            container[host.name] = changes
          }
          return container
        });
      });

      Observable.forkJoin(changes$).subscribe((data) => {
        this.packageChanges = data.reduce((acc, c) => Object.assign({}, acc, c));
      });
    }

    public ngOnDestroy(): void {
      if (this.hostsSub) {
        this.hostsSub.unsubscribe();
      }
    }

    get hasChanges(): boolean {
      return this.packageChanges && Object.keys(this.packageChanges).length > 0;
    }

    get hostNames(): string[] {
      if (this.packageChanges) {
        return Object.keys(this.packageChanges);
      }
      return [];
    }

    public gotoHost(hostName: string) {
      this.router.navigate(['/host', hostName]);
    }

}
