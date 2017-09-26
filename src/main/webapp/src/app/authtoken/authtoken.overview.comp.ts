import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { BehaviorSubject, Observable } from 'rxjs/Rx';

import { AlertService } from '../util/alert/alert.service';
import { AuthToken, AuthTokenHttpService } from '../util/http/authtoken.http.service';

interface AuthTokenSelection extends AuthToken {
  selected: boolean;
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './authtoken.overview.comp.html'
})
export class AuthTokenOverviewComponent implements OnInit {

  public selectionCount = 0;
  private _allSelected = false;
  private _tokens: BehaviorSubject<AuthTokenSelection[]> = new BehaviorSubject([]);

  public tokens: Observable<AuthTokenSelection[]> = this._tokens.asObservable();
  public revokeForm: FormGroup;

  private static toSelection(authToken: AuthToken): AuthTokenSelection {
    return {...authToken, selected: false};
  }

  constructor(private authTokenHttp: AuthTokenHttpService,
              private alertService: AlertService,
              private fb: FormBuilder) {
    this.revokeForm = this.fb.group({ revokeComment: '' });
  }

  ngOnInit(): void {
    this.loadTokens();
  }

  get allSelected() {
    return this._allSelected;
  }

  set allSelected(value) {
    this._allSelected = value;
    const selectable = this._tokens.getValue().filter(t => !t.revoked);
    selectable.forEach(t => t.selected = value);
    this.selectionCount = selectable.filter(t => t.selected).length;
  }

  private loadTokens() {
    this.authTokenHttp.getAuthTokens()
      .map((tokens) => tokens.map(AuthTokenOverviewComponent.toSelection))
      .subscribe((tokenSelections) => {
        this._tokens.next(tokenSelections);
        this.updateSelectionCount();
      });
  }

  public generateToken() {
    this.authTokenHttp.generateToken().subscribe(() => {
      this.alertService.success('Successfully generated new agent auth token!');
      this.loadTokens();
    }, (err) => {
      this.alertService.danger('Error generating new agent auth token!');
      console.error(err);
    });
  }

  public revokeSelectedTokens(formValue) {
    const revokeBatch = this._tokens.getValue().filter(t => t.selected)
      .map((token) => this.authTokenHttp.revokeToken(token.id, formValue.revokeComment));

    // execute revokes in parallel
    Observable.forkJoin(revokeBatch).subscribe(() => {
      this.alertService.success(`Successfully revoked ${revokeBatch.length} agent auth tokens.`);
      this.loadTokens();
    }, (err) => {
      this.alertService.danger(`Error revoking agent auth tokens!`);
      console.error(err);
    });

    // clear revoke comment in form
    this.revokeForm.reset();
  }

  public updateSelectionCount() {
    this.selectionCount = this._tokens.getValue().filter(t => t.selected).length;
  }

}
