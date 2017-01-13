/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { Component, AfterViewInit } from "@angular/core";
import {
  PackageServerHttpService,
  PackageServerGroup,
  PackageServer
} from "../services/http/packageserver.http.service";
import { AlertService } from "../services/alert/alert.service";

@Component({
  moduleId: module.id,
  selector: 'ps-overview',
  templateUrl: 'html/ps.overview.component.html'
})
export class PackageServersOverview implements AfterViewInit {

  private _searchQuery: string = null;

  private _groups: Array<PackageServerGroup> = []

  constructor(private http: PackageServerHttpService) {
  };

  ngAfterViewInit(): void {
    this.loadData();
  }

  private loadData() {
    this.http.getGroups().subscribe(
      (result) => this.groups = result
    )
  }

  private deleteServerGroup(id:number) {
    console.log("delete server group with id "+ id);
  }
  private deleteServer(id:number):void {
    console.log("delete server with id "+ id);
  }


  get groups(): Array<PackageServerGroup> {
    return this._groups;
  }

  set groups(value: Array<PackageServerGroup>) {
    this._groups = value
      .filter(group => this.filterData(group, this._searchQuery))
      .sort(this.sortGroup);

    for (let group of this._groups) {
      group.packageServers = group.packageServers.sort(this.sortServer);
    }
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadData();
  }


  private sortGroup(a: PackageServerGroup, b: PackageServerGroup) {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  private filterData(group: PackageServerGroup, query: string) {
    if (query != null && query.trim().length > 0) {
      return group.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  private sortServer(a: PackageServer, b: PackageServer) {
    if (a.description < b.description) return -1;
    if (a.description > b.description) return 1;
    return 0;
  }
}
