import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

import { Host, HostHttpService } from '../util/http/host.http.service';
import { Package, PackageHttpService } from '../util/http/package.http.service';
import { Repo, RepoHttpService } from '../util/http/repo.http.service';
import { Service, ServiceHttpService } from '../util/http/service.http.service';
import { SettingHttpService, Settings } from '../util/http/setting.http.service';
import { Sorter } from '../util/sorters.util';
import { StatsHttpService, Stats } from '../util/http/stats.http.service';
import { TemplateHttpService, Template } from '../util/http/template.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  templateUrl: './home.comp.html'
})
export class HomeComponent implements OnInit, OnDestroy {

  private onDestroy$: Subject<void> = new Subject();

  private hostTimer = new Subject<number>();
  private scanTimer = new Subject<number>();
  private serviceTimer = new Subject<number>();
  private statsTimer = new Subject<number>();

  public hosts$: Observable<Host[]>;
  public services$: Observable<Service[]>;
  public repos$: Observable<Repo[]>;
  public stats$: Observable<Stats>;

  constructor(private router: Router,
              private settingHttpService: SettingHttpService,
              private hostHttpService: HostHttpService,
              private serviceHttpService: ServiceHttpService,
              private repoHttpService: RepoHttpService,
              private statsHttpService: StatsHttpService) { };

  ngOnInit(): void {
    this.loadSettings().subscribe(
      (settings) => {
        const pageRefreshInterval = SettingHttpService.calcIntervalInMillis(settings.pageRefreshTimer, settings.pageRefreshTimerUnit);
        this.hostTimer.next(pageRefreshInterval);
        this.serviceTimer.next(pageRefreshInterval);
        this.statsTimer.next(pageRefreshInterval);

        const scanInterval = SettingHttpService.calcIntervalInMillis(settings.indexScanTimer, settings.indexScanTimerUnit);
        this.scanTimer.next(scanInterval);
      },
      (err) => {
        console.error(err);
      }
    );

    this.hosts$ = this.hostTimer.switchMap((interval) => {
        return Observable.interval(interval).startWith(0).takeUntil(this.onDestroy$).flatMap(() => this.loadHosts());
    });

    this.repos$ = this.scanTimer.switchMap((interval) => {
        return Observable.interval(interval).startWith(0).takeUntil(this.onDestroy$).flatMap(() => this.loadRepos())
    });

    this.services$ = this.serviceTimer.switchMap((interval) => {
      return Observable.interval(interval).startWith(0).takeUntil(this.onDestroy$).flatMap(() => this.loadServices());
    });

    this.stats$ = this.statsTimer.switchMap((interval) => {
      return Observable.interval(interval).startWith(0).takeUntil(this.onDestroy$).flatMap(() => this.loadStats());
    });
  }

  public loadSettings(): Observable<Settings> {
    return this.settingHttpService.getSettings();
  }

  public loadHosts(): Observable<Host[]> {
    return this.hostHttpService.getSimpleHosts()
                              .map(hosts => hosts.sort((a, b) => Sorter.byField(a, b, 'lastSeen')));
  }

  public loadRepos(): Observable<Repo[]> {
    return this.repoHttpService.getRepos()
                              .map(repos => repos.sort((a, b) => Sorter.byField(a, b, 'lastIndex')));
  }

  public loadServices(): Observable<Service[]> {
    return this.serviceHttpService.getServices().map(services => services.sort(Sorter.service))
    .flatMap(services => {
      const usages$: Observable<any>[] = services.map(s => this.serviceHttpService.getServiceUsage(s.name));

      return Observable.forkJoin(usages$).map(usages => {
        return usages.map((usage, index) => {
            let ts = (usage) ? Object.keys(usage) : [];
            return {...services[index], templates: ts};
        });
      });
    });
  }

  public loadStats(): Observable<Stats> {
    return this.statsHttpService.getStats()
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
  }

  public gotoHost(hostUuid: string) {
    this.router.navigate(['/host', hostUuid]);
  }

  public gotoService(serviceName: string) {
    this.router.navigate(['/service', serviceName]);
  }

  public gotoRepo(repoName: string) {
    this.router.navigate(['/repo', repoName]);
  }

}
