import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { forkJoin, Observable } from 'rxjs';
import { finalize, map, mergeMap } from 'rxjs/operators';

import { AlertService } from '../util/alert/alert.service';
import { Service, ServiceHttpService, ServiceUsage } from '../util/http/service.http.service';
import { Sorter } from '../util/sorters.util';
import { Validator } from '../util/validator.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
    selector: 'service-overview',
    templateUrl: './service.overview.comp.html',
    standalone: false
})
export class ServiceOverview implements OnInit {

  private _searchQuery: string = null;

  private _services: Service[] = [];

  public servicesLoaded = false;

  private static filterData(service: Service, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return service.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  constructor(private readonly serviceHttp: ServiceHttpService,
              private readonly router: Router,
              private readonly alerts: AlertService) { };

  public ngOnInit(): void {
      this.loadData();
  }

  private loadData() {
    this.serviceHttp.getServices().pipe(
      mergeMap(services => {
        this.services = services;

        const usageOps: Observable<ServiceUsage>[] = services.map(service => this.serviceHttp.getServiceUsage(service.name));
        return forkJoin(usageOps).pipe(
          map((usages) => {
            return usages.map((usage, index) => {
              const usingTemplates = Object.keys(usage);
              return {...services[index], templates: usingTemplates};
            });
          })
        );
      }),
      finalize(() => this.servicesLoaded = true)
    ).subscribe(
      (services) => {
        this.services = services;
      }, (err) => {
        this.alerts.danger('Error loading services!');
        console.error(err);
      }
    );
  }

  get services(): Array<Service> {
    return this._services;
  }

  set services(value: Array<Service>) {
    this._services = value
      .filter(repo => ServiceOverview.filterData(repo, this._searchQuery))
      .sort(Sorter.service);
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadData();
  }

  public gotoDetails(service: Service): void {
    if (Validator.notEmpty(service.name)) {
      this.router.navigate(['service', service.name]);
    }
  }

  public deleteService(service: Service): void {
    if (service) {
      this.serviceHttp.deleteService(service)
        .subscribe(() =>  {
          this.alerts.success(`Successfully deleted service '${service.name}'!`);
          this.loadData();
        },
        (err) => {
          this.alerts.danger(`Error deleting service '${service.name}'!`);
          console.error(err);
        });
    }
  }

}
