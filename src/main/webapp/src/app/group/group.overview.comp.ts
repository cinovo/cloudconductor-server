import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { Subscription } from 'rxjs';

import { GroupHttpService, Group } from '../util/http/group.http.service';
import { AlertService } from '../util/alert/alert.service';
import { Validator } from '../util/validator.util';
import { Sorter } from '../util/sorters.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './group.overview.comp.html'
})
export class GroupOverviewComponent implements OnInit, OnDestroy {

  public permissions: string[] = [];
  public groups: Group[];

  private _searchQuery = '';
  private _permissionQuery = '';
  private _groupSub: Subscription;

  private static filterData(group: Group, query: string): boolean {
    const queryString = query.trim();
    if (Validator.notEmpty(queryString)) {
      return group.name && group.name.includes(queryString);
    }
    return true;
  }

  private static filterPermission(group: Group, permissionQuery: string): boolean {
    const queryString = permissionQuery.trim();
    if (Validator.notEmpty(queryString)) {
      return group.permissions && group.permissions.length > 0 && group.permissions.includes(queryString);
    }
    return true;
  }

  constructor(private groupHttp: GroupHttpService,
              private alertService: AlertService,
              private router: Router) { }

  ngOnInit(): void {
    this.reloadGroups();
  }

  private reloadGroups(): void {
    this._groupSub = this.groupHttp.getGroups().subscribe(
      (groups) => {
        const permissionSet = groups.reduce((acc, g) => new Set<string>([...Array.from(acc), ...g.permissions]), new Set<string>());
        this.permissions = Array.from(permissionSet).sort();

        this.groups = groups.filter(g => GroupOverviewComponent.filterData(g, this._searchQuery))
                            .filter(g => GroupOverviewComponent.filterPermission(g, this._permissionQuery))
                            .map(g => {
                              g.permissions = g.permissions.sort();
                              return g;
                            })
                            .sort((a, b) => Sorter.byField(a, b, 'name'));
      }, (err) => {
        this.alertService.danger('Error loading user groups!');
        console.error(err);
      }
    );
  }

  ngOnDestroy(): void {
    if (this._groupSub) {
      this._groupSub.unsubscribe();
    }
  }

  get searchQuery() {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.reloadGroups();
  }

  get permissionQuery() {
    return this._permissionQuery;
  }

  set permissionQuery(value: string) {
    this._permissionQuery = value;
    this.reloadGroups();
  }

  public goToGroup(group: Group): void {
    if (group && group.name) {
      this.router.navigate(['/group', group.name]);
    }
  }

  public deleteGroup(groupToDelete: Group): void {
    this.groupHttp.deleteGroup(groupToDelete.name).subscribe(
      () => {
        this.alertService.success(`Successfully deleted user group '${groupToDelete.name}'!`);
        this.reloadGroups();
      }, (err) => {
        console.error(err);
        this.alertService.danger(`Error deleting user group '${groupToDelete.name}'!`);
      }
    );
  }

}
