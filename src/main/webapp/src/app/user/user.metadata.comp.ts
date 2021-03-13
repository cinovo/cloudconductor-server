import { Location } from '@angular/common';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { throwError as observableThrowError, Observable, Subscription, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { AlertService } from '../util/alert/alert.service';
import { GroupHttpService } from '../util/http/group.http.service';
import { User, UserHttpService } from '../util/http/user.http.service';
import { Mode } from '../util/enums.util';
import { forbiddenNamesValidator, Validator } from '../util/validator.util';

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
  public userForm: FormGroup;
  public showNewGroup = false;
  public newGroup = '';
  public allGroups: string[] = [];
  public modes = Mode;

  private _userSub: Subscription;
  private _groupSub: Subscription;

  constructor(private readonly fb: FormBuilder,
              private readonly location: Location,
              private readonly router: Router,
              private readonly userHttp: UserHttpService,
              private readonly groupHttp: GroupHttpService,
              private readonly alertService: AlertService) {
  }

  ngOnInit(): void {
    this.userForm = this.fb.group({
      loginName: ['', [Validators.required, forbiddenNamesValidator(['new', 'admin', 'agent'], this.mode === Mode.NEW)]],
      displayName: [''],
      email: [''],
      newPassword: [''],
      repeatPassword: [''],
      registrationDate: [''],
      active: [false],
      newGroup: ['']
    });

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
      loginName: userForm.loginName.trim(),
      displayName: userForm.displayName.trim(),
      email: userForm.email.trim(),
      userGroups: this.user.userGroups,
      active: userForm.active
    };

    if (userForm.newPassword && userForm.newPassword.length > 0) {
      if (userForm.newPassword === userForm.repeatPassword) {
        u.password = userForm.newPassword
      } else {
        this.alertService.danger('Passwords do not match!');
        return;
      }
    }

    const check: Observable<boolean> = (this.mode === Mode.NEW) ? this.userHttp.existsUser(u.loginName) : of(false);
    check.pipe(
      switchMap((exists) => exists ? observableThrowError(`User named '${u.loginName}' does already exist!`) : this.userHttp.saveUser(u))
    ).subscribe(
      () => {
        this.alertService.success(`Successfully saved user '${u.loginName}'!`);
        if (this.mode === Mode.NEW) {
          this.userForm.reset();
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['/user', u.loginName]);
        } else {
          // only clear new password
          this.userForm.controls.newPassword.reset();
          this.userForm.controls.repeatPassword.reset();
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
