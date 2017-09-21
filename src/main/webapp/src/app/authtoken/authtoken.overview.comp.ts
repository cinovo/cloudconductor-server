import { AlertService } from '../util/alert/alert.service';
import { Component, OnInit } from '@angular/core';

import { BehaviorSubject, Observable, Subject } from 'rxjs/Rx';

import { AuthToken, AuthTokenHttpService } from '../util/http/authtoken.http.service';

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

  private _tokens: Subject<AuthToken[]> = new BehaviorSubject([]);
  public tokens: Observable<AuthToken[]> = this._tokens.asObservable();

  constructor(private authTokenHttp: AuthTokenHttpService,
              private alertService: AlertService) { }

  ngOnInit(): void {
      this.loadTokens();
  }

  private loadTokens() {
    this.authTokenHttp.getAuthTokens().subscribe((authTokens) => {
      this._tokens.next(authTokens);
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

  public revokeSelectedTokens() {
    // TODO not implemented yet!
  }

}
