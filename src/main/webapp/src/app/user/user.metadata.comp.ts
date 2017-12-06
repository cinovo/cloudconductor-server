import { Location } from '@angular/common';
import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { GroupHttpService } from '../util/http/group.http.service';
import { User, UserHttpService } from '../util/http/user.http.service';
import { Mode } from '../util/enums.util';
import { Validator, forbiddenNameValidator, forbiddenNamesValidator } from '../util/validator.util';

interface UserForm {
  loginName: string,
  displayName: string,
  email: string,
  newPassword: string,
  repeatPassword: string,
  registrationDate: string,
  active: boolean,
  newGroup: string
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'user-metadata',
  templateUrl: './user.metadata.comp.html'
})
export class UserMetaDataComponent implements OnInit, OnDestroy {

  @Input() userObs: Observable<User>;
  @Input() mode: Mode;

  public user: User;
  public modes = Mode;
  public userForm: FormGroup;
  public showNewGroup = false;
  public newGroup = '';
  public allGroups: string[] = [];

  private _userSub: Subscription;
  private _groupSub: Subscription;

  constructor(private fb: FormBuilder,
              private location: Location,
              private router: Router,
              private userHttp: UserHttpService,
              private groupHttp: GroupHttpService,
              private alertService: AlertService) {
    this.userForm = this.fb.group({
      'loginName': ['', [Validators.required, forbiddenNamesValidator(['new', 'admin', 'agent'])]],
      'displayName': [''],
      'email': [''],
      'newPassword': [''],
      'repeatPassword': [''],
      'registrationDate': [''],
      'active': [false],
      'newGroup': ['']
    });
  }

  ngOnInit(): void {
    this._userSub = this.userObs.subscribe(
      (user) => {
        if (user) {
          this.user = user;
          this.userForm.patchValue(user);
        }
      }, (err) => {
        console.error(err);
      }
    );

    this._groupSub = this.groupHttp.getGroupNames().subscribe(
      (groupNames) => this.allGroups = groupNames,
      (err) => console.error(err)
    );
  }

  ngOnDestroy(): void {
    this.userForm.reset();

    if (this._userSub) {
      this._userSub.unsubscribe();
    }

    if (this._groupSub) {
      this._groupSub.unsubscribe();
    }
  }

  public goBack(): void {
    this.userForm.reset();
    this.location.back();
  }

  public saveUser(userForm: UserForm): void {
    const u: User = {
      loginName: userForm.loginName,
      displayName: userForm.displayName,
      email: userForm.email,
      userGroups: this.user.userGroups,
      active: userForm.active
    };

    if (this.mode === this.modes.NEW) {
      if (userForm.newPassword && userForm.newPassword.length > 0) {
        if (userForm.newPassword === userForm.repeatPassword) {
          u.password = userForm.newPassword
        } else {
          this.alertService.danger('Passwords do not match!');
          return;
        }
      }
    }

    const check: Observable<boolean> = (this.mode === this.modes.NEW) ? this.userHttp.existsUser(u.loginName) : Observable.of(false);

    check.flatMap((exists) => {
      if (exists) {
        return Observable.throw(`User named '${u.loginName}' does already exist!`);
      } else {
        return this.userHttp.saveUser(u);
      }
    }).subscribe(
      () => {
        this.alertService.success(`Successfully saved user '${u.loginName}'!`);
        if (this.mode === this.modes.NEW) {
          this.userForm.reset();
          this.router.navigate(['/user', u.loginName]);
        }
      }, (err) => {
        this.alertService.danger(`Error saving user '${u.loginName}': ${err}`);
        console.error(err);
      }
    );
  }

  public goToNewGroup(): void {
    this.newGroup = '';
    this.showNewGroup = true;
    this.groupHttp.getGroupNames().subscribe(
      (groupNames) => {
        this.allGroups = groupNames.filter(g => !this.user.userGroups.includes(g))
                                  .sort();
        if (this.allGroups.length > 0) {
          this.newGroup = this.allGroups[0];
        }
      }
    );
  }

  public removeGroup(groupName: string): void {
    if (Validator.notEmpty(groupName)) {
      const sortedGroups = this.user.userGroups.slice().sort();
      const indexToDelete = sortedGroups.indexOf(groupName);
      if (indexToDelete > -1) {
        sortedGroups.splice(indexToDelete, 1);
        this.user.userGroups = sortedGroups;
        if (this.showNewGroup) {
          this.goToNewGroup();
        }
      }
    }
  }

  public addNewGroup(groupName: string): void {
    if (groupName) {
      const userGroups = this.user.userGroups.slice();
      userGroups.push(groupName);
      userGroups.sort();

      this.user.userGroups = userGroups;
      this.newGroup = null;
      this.showNewGroup = false;
    }
  }

}
