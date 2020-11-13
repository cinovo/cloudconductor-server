
import {throwError as observableThrowError, of as observableOf,  Observable ,  Subscription } from 'rxjs';

import {mergeMap} from 'rxjs/operators';
import { Location } from '@angular/common';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AlertService } from '../util/alert/alert.service';
import { Group, GroupHttpService } from '../util/http/group.http.service';
import { PermissionHttpService } from '../util/http/permission.http.service';
import { Mode } from '../util/enums.util';
import { forbiddenNamesValidator } from '../util/validator.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'group-metadata',
  templateUrl: './group.metadata.comp.html'
})
export class GroupMetaDataComponent implements OnInit, OnDestroy {

  @Input() groupObs: Observable<Group>;
  @Input() mode: Mode;
  @Output() onReload: EventEmitter<string> = new EventEmitter();

  public userGroupForm: FormGroup;
  public modes = Mode;
  public group: Group;
  public permissions: string[] = [];

  private _groupSub: Subscription;

  constructor(private fb: FormBuilder,
              private location: Location,
              private router: Router,
              private groupHttp: GroupHttpService,
              private permissionHttp: PermissionHttpService,
              private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.userGroupForm = this.fb.group({
      name: ['', [Validators.required, forbiddenNamesValidator(['new', 'Administrator', 'Agent', 'Anonymous'], this.mode === Mode.NEW)]],
      description: ['']
    });

    this._groupSub = this.groupObs.subscribe(
      (group) => {
        group.permissions = group.permissions.slice().sort();
        this.group = group;
        this.userGroupForm.patchValue(group);
      }
    );

    this.permissionHttp.getPermissions().subscribe(
      (permissions) => {
        this.permissions = permissions.sort();
      }, (err) => {
        console.error(err);
      }
    );
  }

  ngOnDestroy(): void {
    if (this._groupSub) {
      this._groupSub.unsubscribe();
    }
  }

  public addPermission(newPermission: string): void {
    const newPermissions = this.group.permissions.slice();
    newPermissions.push(newPermission);
    newPermissions.sort();
    this.group.permissions = newPermissions;
  }

  public removePermission(permissionToRemove: string): void {
    const sortedPermissions = this.group.permissions.slice().sort();
    const indexToDelete = sortedPermissions.indexOf(permissionToRemove);
    if (indexToDelete > -1) {
      sortedPermissions.splice(indexToDelete, 1);
      this.group.permissions = sortedPermissions;
    }
  }

  public saveGroup(groupForm: Group): void {
    const groupToSave = Object.assign({}, this.group, groupForm);
    groupToSave.name = groupToSave.name.trim();
    const groupName = groupToSave.name;

    const check: Observable<boolean> = (this.mode === this.modes.NEW) ? this.groupHttp.existsGroup(groupName) : observableOf(false);

    check.pipe(mergeMap((exists) => {
      if (exists) {
        return observableThrowError(`A user group named '${groupName}' already exists!`);
      } else {
        return this.groupHttp.saveGroup(groupToSave)
      }
    })).subscribe(
      () => {
        this.alertService.success(`Successfully saved user group '${groupName}'.`);
        this.userGroupForm.reset();

        if (this.mode === this.modes.NEW) {
          this.router.navigate(['/group', groupName]);
        } else {
          this.onReload.emit(groupName);
        }
      },
      (err) => {
        this.alertService.danger(`Error saving user group '${groupName}'!`);
        console.error(err);
      }
    );
  }

  public goBack(): void {
    this.location.back();
  }

}
