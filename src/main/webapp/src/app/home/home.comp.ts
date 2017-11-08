import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Subject } from 'rxjs/Rx';
import { Observable } from 'rxjs/Observable';

import { Host, HostHttpService } from '../util/http/host.http.service';
import { Sorter } from '../util/sorters.util';
import { Hosts } from '../util/hosts.util';
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

    this.services$ = this.serviceHttpService.getServices();
  }

  public loadHosts(): Observable<Host[]> {
    return this.hostHttpService.getHosts()
                              .map(hosts => hosts.sort((a, b) => Sorter.byField(a, b, 'lastSeen')));
  }

  public loadRepos(): Observable<Repo[]> {
    return this.repoHttpService.getRepos()
                              .map(repos => repos.sort((a, b) => Sorter.byField(a, b, 'lastIndex')));
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
  }

  public isAlive(host: Host): boolean {
    return Hosts.isAlive(host);
  }

  public gotoHost(host: Host) {
    this.router.navigate(['/host', host.name]);
  }

  public gotoService(service: Service) {
    this.router.navigate(['/service', service.name]);
  }

  public gotoRepo(repo: Repo) {
    this.router.navigate(['/repo', repo.name]);
  }

  public scanIsDue(repo: Repo): boolean {
    const diff = new Date().getTime() - repo.lastIndex;
    return diff > this.scanInterval;
  }

  public scanIsPastDue(repo: Repo): boolean {
    const diff = new Date().getTime() - repo.lastIndex;
    return diff > 2 * this.scanInterval;
  }
}
