import { Component, OnDestroy, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

import { HostHttpService, Host } from '../util/http/host.http.service';
import { PackageChange, PackageChangesService } from '../util/packagechanges/packagechanges.service';
import { SettingHttpService } from '../util/http/setting.http.service';
import { TemplateHttpService } from '../util/http/template.http.service';

interface PackageChangeMap { [key: string]: PackageChange[] };

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'home-packagechanges',
  templateUrl: 'home.pkgchanges.comp.html'
})
export class HomePackageChangesComponent implements OnInit, OnDestroy {

  public packageChanges: PackageChangeMap;
  public lastUpdate: number;

  @Input() hostsObs: Observable<Host[]>;
  @Output() onHostClicked: EventEmitter<string> = new EventEmitter<string>();

  private hostsSub: Subscription;

  constructor(private packageChangesService: PackageChangesService) { }

    public ngOnInit(): void {
      this.hostsSub = this.hostsObs.subscribe((hosts) => {
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
          return container;
        });
      });

      Observable.forkJoin(changes$).subscribe(
        (data) => {
          this.packageChanges = data.reduce((acc, c) => Object.assign({}, acc, c));
          this.lastUpdate = new Date().getTime();
        },
        (err) => console.error(err),
        () => {
          // if there are no changes, forkJoin observable will complete immediatly
          this.lastUpdate = new Date().getTime();
        }
      );
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

}
