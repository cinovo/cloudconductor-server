import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AlertService } from '../util/alert/alert.service';
import { Service, ServiceHttpService } from '../util/http/service.http.service';
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
  templateUrl: './service.overview.comp.html'
})
export class ServiceOverview implements OnInit {

  private _searchQuery: string = null;

  private _services: Array<Service> = [];

  private static filterData(service: Service, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return service.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  constructor(private serviceHttp: ServiceHttpService,
              private router: Router,
              private alerts: AlertService) { };

  public ngOnInit(): void {
      this.loadData();
  }

  private loadData() {
    this.serviceHttp.getServices().subscribe(
      (result) => this.services = result
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

  protected gotoDetails(service: Service) {
    if (Validator.notEmpty(service.name)) {
      this.router.navigate(['service', service.name]);
    }
  }

  protected deleteService(service: Service): void {
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
