import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Subject } from 'rxjs/Rx';
import { Observable } from 'rxjs/Observable';

import { Host, HostHttpService } from '../util/http/host.http.service';
import { Sorter } from '../util/sorters.util';
import { Service, ServiceHttpService } from '../util/http/service.http.service';
import { Repo, RepoHttpService } from '../util/http/repo.http.service';

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

  // TODO get this from Settings
  private hostInterval = 60 * 1000;
  private scanInterval = 60 * 1000;
  private serviceInterval = 60 * 1000;

  private onDestroy$: Subject<void> = new Subject();

  public hosts$: Observable<Host[]>;
  public services$: Observable<Service[]>;
  public repos$: Observable<Repo[]>;

  constructor(private router: Router,
              private hostHttpService: HostHttpService,
              private serviceHttpService: ServiceHttpService,
              private repoHttpService: RepoHttpService) { };

  ngOnInit(): void {
    this.hosts$ = Observable.interval(this.hostInterval).startWith(0).takeUntil(this.onDestroy$).flatMap(() => this.loadHosts());
    this.repos$ = Observable.interval(this.scanInterval).startWith(0).takeUntil(this.onDestroy$).flatMap(() => this.loadRepos());
    this.services$ = Observable.interval(this.serviceInterval).startWith(0).takeUntil(this.onDestroy$).flatMap(() => this.loadServices());
  }

  public loadHosts(): Observable<Host[]> {
    return this.hostHttpService.getHosts()
                              .map(hosts => hosts.sort((a, b) => Sorter.byField(a, b, 'lastSeen')));
  }

  public loadRepos(): Observable<Repo[]> {
    return this.repoHttpService.getRepos()
                              .map(repos => repos.sort((a, b) => Sorter.byField(a, b, 'lastIndex')));
  }

  public loadServices(): Observable<Service[]> {
    return this.serviceHttpService.getServices().map(services => services.sort(Sorter.service));
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
  }

  public gotoHost(hostName: string) {
    this.router.navigate(['/host', hostName]);
  }

  public gotoService(serviceName: string) {
    this.router.navigate(['/service', serviceName]);
  }

  public gotoRepo(repoName: string) {
    this.router.navigate(['/repo', repoName]);
  }

}
