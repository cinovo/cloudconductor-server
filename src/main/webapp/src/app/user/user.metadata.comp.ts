import { Location } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { AlertService } from '../util/alert/alert.service';
import { GroupHttpService } from '../util/http/group.http.service';
import { User, UserHttpService } from '../util/http/user.http.service';
import { Mode } from '../util/enums.util';
import { Validator } from '../util/validator.util';

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
export class UserMetaDataComponent implements OnInit {

  @Input() userObs: Observable<User>;
  @Input() mode: Mode;

  public user: User;
  public modes = Mode;
  public userForm: FormGroup;
  public showNewGroup = false;
  public newGroup = '';
  public allGroups: string[] = [];

  constructor(private fb: FormBuilder,
              private location: Location,
              private router: Router,
              private userHttp: UserHttpService,
              private groupHttp: GroupHttpService,
              private alertService: AlertService) {
    this.userForm = this.fb.group({
      'loginName': ['', Validators.required],
      'displayName': [''],
      'email': [''],
      'registrationDate': [''],
      'active': [false],
      'newGroup': ['']
    });
  }

  ngOnInit(): void {
    this.userObs.subscribe(
      (user) => {
        if (user) {
          this.user = user;
          this.userForm.patchValue(user);
        }
      }, (err) => {
        console.error(err);
      }
    );

    this.groupHttp.getGroupNames().subscribe(
      (groupNames) => this.allGroups = groupNames,
      (err) => console.error(err)
    );
  }

  public goBack(): void {
    this.location.back();
  }

  public saveUser(userToSave: User): void {
    const u = {
      loginName: userToSave.loginName,
      displayName: userToSave.displayName,
      email: userToSave.email,
      userGroups: this.user.userGroups
    };
    console.log({u});
    this.userHttp.saveUser(u).subscribe(
      () => {
        this.alertService.success(`Successfully saved user '${u.loginName}'!`);
        this.userForm.reset();
        if (this.mode === this.modes.NEW) {
          this.router.navigate(['/user', u.loginName]);
        }
      }, (err) => {
        this.alertService.danger(`Error saving user '${u.loginName}'!`);
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
      this.user.userGroups.splice(this.user.userGroups.indexOf(groupName), 1);
      if (this.showNewGroup) {
        this.goToNewGroup();
      }
    }
  }

  public addNewGroup(groupName: string): void {
    if (groupName) {
      this.user.userGroups.push(groupName);
      this.user.userGroups.sort();
      this.newGroup = null;
      this.showNewGroup = false;
    }
  }

}
