import { Component, AfterViewInit } from "@angular/core";
import { Service, ServiceHttpService } from "../services/http/service.http.service";
import { Router } from "@angular/router";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";
/**
 * Created by psigloch on 11.01.2017.
 */

@Component({
  moduleId: module.id,
  selector: 'service-overview',
  templateUrl: 'html/service.overview.comp.html'
})
export class ServiceOverview implements AfterViewInit {

  private _searchQuery: string = null;

  private _services: Array<Service> = [];

  constructor(private serviceHttp: ServiceHttpService, private router: Router) {
  };

  ngAfterViewInit(): void {
    this.loadData();
  }

  private loadData() {
    this.serviceHttp.getServices().subscribe(
      (result) => this.services = result
    )
  }


  get services(): Array<Service> {
    return this._services;
  }

  set services(value: Array<Service>) {
    this._services = value
      .filter(group => ServiceOverview.filterData(group, this._searchQuery))
      .sort(Sorter.sortService);
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadData();
  }

  private gotoDetails(service: Service) {
    if (Validator.notEmpty(service.name)) {
      this.router.navigate(['services', service.name]);
    }
  }

  private deleteService(service: Service): void {
    if (service) {
      this.serviceHttp.deleteService(service);
    }
  }

  private static filterData(service: Service, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return service.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }
}
