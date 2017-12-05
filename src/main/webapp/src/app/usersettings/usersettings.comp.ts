import { Location } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { AuthTokenProviderService } from '../util/auth/authtokenprovider.service';
import { UserHttpService, PasswordChangeRequest } from '../util/http/user.http.service';

interface PwChangeForm {
  oldPassword: string,
  newPassword: string,
  repeatPassword: string
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './usersettings.comp.html'
})
export class UserSettingsComponent implements OnInit, OnDestroy {

  public passwordForm: FormGroup;

  private _userSub: Subscription;
  private _username: string;

  constructor(private fb: FormBuilder,
              private location: Location,
              private alertService: AlertService,
              private authTokenProvider: AuthTokenProviderService,
              private userHttp: UserHttpService) {
    this.passwordForm = fb.group({
      'oldPassword': ['', Validators.required],
      'newPassword': ['', Validators.required],
      'repeatPassword': ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this._userSub = this.authTokenProvider.currentUser.subscribe(
      (user) => this._username = user.name
    );
  }

  ngOnDestroy(): void {
    if (this._userSub) {
      this._userSub.unsubscribe();
    }
  }

  public goBack(): void {
    this.passwordForm.reset();
    this.location.back();
  }

  public changePassword(pws: PwChangeForm): void {
    if (pws.newPassword !== pws.repeatPassword) {
      this.alertService.danger('Error: Passwords do not match!');
      return;
    }

    const changeRequest: PasswordChangeRequest = { oldPassword: pws.oldPassword, newPassword: pws.newPassword, username: this._username };
    this.userHttp.changePassword(changeRequest).subscribe(
      () => {
        this.passwordForm.reset();
        this.alertService.success(`Successfully changed password for user '${changeRequest.username}'.`);
      }, (err) => {
        this.passwordForm.reset();
        this.alertService.danger(`Error changing password for user '${changeRequest.username}'!`);
        console.error(err);
      }
    );
  }

}
