/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { Component, AfterViewInit } from "@angular/core";
import { PackageServerHttpService } from "../services/http/packageserver.http.service";
import { PackageServerGroup, PackageServerGroupHttpService } from "../services/http/packageservergroup.http.service";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";

@Component({
  moduleId: module.id,
  selector: 'ps-overview',
  templateUrl: 'html/ps.overview.comp.html'
})
export class PackageServersOverview implements AfterViewInit {

  private _searchQuery: string = null;

  private _groups: Array<PackageServerGroup> = [];

  constructor(private groupHttp: PackageServerGroupHttpService, private serverHttp: PackageServerHttpService) {
  };

  ngAfterViewInit(): void {
    this.loadData();
  }

  private loadData() {
    this.groupHttp.getGroups().subscribe(
      (result) => this.groups = result
    )
  }

  private deleteServerGroup(id: number) {
    if (Validator.idIsSet(id)) {
      this.groupHttp.deleteGroup(id.toString()).subscribe((result) => this.loadData());
    }
  }

  private deleteServer(id: number): void {
    if (Validator.idIsSet(id)) {
      this.serverHttp.deletePackageServer(id.toString()).subscribe((result) => this.loadData());
    }
  }

  get groups(): Array<PackageServerGroup> {
    return this._groups;
  }

  set groups(value: Array<PackageServerGroup>) {
    this._groups = value
      .filter(group => PackageServersOverview.filterData(group, this._searchQuery))
      .sort(Sorter.group);

    for (let group of this._groups) {
      group.packageServers = group.packageServers.sort(Sorter.server);
    }
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadData();
  }

  private static filterData(group: PackageServerGroup, query: string) {
    if (Validator.notEmpty(query)) {
      return group.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

}
