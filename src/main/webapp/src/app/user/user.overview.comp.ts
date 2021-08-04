import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { Subject, Subscription } from 'rxjs';

import { AlertService } from '../util/alert/alert.service';
import { UserHttpService, User } from '../util/http/user.http.service';
import { GroupHttpService } from '../util/http/group.http.service';
import { Validator } from '../util/validator.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './user.overview.comp.html'
})
export class UserOverviewComponent implements OnInit, OnDestroy {

  private _searchQuery = '';
  private _groupQuery = '';

  private _usersSubject: Subject<User[]> = new Subject();

  private _userSub: Subscription;
  private _groupSub: Subscription;

  public users$ = this._usersSubject.asObservable();

  public groups: string[] = [];

  private static filterData(user: User, query: string): boolean {
    const queryString = query.trim();
    if (Validator.notEmpty(queryString)) {
      return (user.loginName && user.loginName.includes(queryString)) ||
            (user.displayName && user.displayName.includes(queryString)) ||
            (user.email && user.email.includes(queryString));
    }
    return true;
  }

  private static filterByGroup(user: User, groupQuery: string): boolean {
    if (Validator.notEmpty(groupQuery)) {
      return user.userGroups.includes(groupQuery);
    }
    return true;
  }

  constructor(private readonly userHttp: UserHttpService,
              private readonly groupHttp: GroupHttpService,
              private readonly alertService: AlertService,
              private readonly router: Router) { }

  ngOnInit(): void {
    this.reloadData();

    this._groupSub = this.groupHttp.getGroupNames().subscribe(
      (groupNames) => this.groups = groupNames.sort(),
      (err) => console.error(err)
    );
  }

  ngOnDestroy(): void {
    if (this._userSub) {
      this._userSub.unsubscribe();
    }

    if (this._groupSub) {
      this._groupSub.unsubscribe();
    }
  }

  get searchQuery() {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.reloadData();
  }

  get groupQuery() {
    return this._groupQuery;
  }

  set groupQuery(value: string) {
    this._groupQuery = value;
    this.reloadData();
  }

  private reloadData(): void {
    this.userHttp.getUsers().subscribe(
      (users) => {
        const filteredUsers = users.filter(u => UserOverviewComponent.filterData(u, this._searchQuery))
                                  .filter(u => UserOverviewComponent.filterByGroup(u, this._groupQuery));

        this._usersSubject.next(filteredUsers);
      }, (err) => {
        this.alertService.danger('Error loading users!');
        console.error(err);
      }
    );
  }

  public deleteUser(userToDelete: User): void {
    const username = userToDelete.loginName;
    this.userHttp.deleteUser(userToDelete.loginName).subscribe(
      () => {
        this.alertService.success(`Successfully deleted user '${username}'.`);
        this.reloadData();
      }, (err) => {
        this.alertService.danger(`Error deleting user '${username}'!`);
        console.error(err);
      }
    );
  }

  public goToDetails(user: User) {
    if (user && user.loginName) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/user', user.loginName]);
    }
  }

}
