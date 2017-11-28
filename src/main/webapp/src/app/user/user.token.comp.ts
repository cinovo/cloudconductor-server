import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { AlertService } from '../util/alert/alert.service';
import { AuthToken, User, UserHttpService } from '../util/http/user.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'user-token',
  templateUrl: './user.token.comp.html'
})
export class UserTokenComponent implements OnInit {

  @Input() userObs: Observable<User>;
  @Output() onReload: EventEmitter<string>= new EventEmitter();

  public username: string;

  constructor(private userHttp: UserHttpService,
              private alertService: AlertService) { }

  ngOnInit(): void {
    this.userObs.subscribe(user => {
      if (user && user.loginName) {
        this.username = user.loginName;
      }
    });
  }

  public addAuthToken() {
    this.userHttp.createAuthToken(this.username).subscribe(
      () => {
        this.alertService.success(`Successfully created new auth token for user '${this.username}'.`);
        this.onReload.emit(this.username);
      },
      (err) => {
        this.alertService.danger(`Error creating auth token for user '${this.username}'!`);
        console.error(err);
      }
    );
  }

  public revokeAuthToken(authToken: AuthToken) {
    this.userHttp.revokeAuthToken(this.username, authToken).subscribe(
      () => {
        this.alertService.success(`Successfully revoked token for user '${this.username}'`);
        this.onReload.emit(this.username);
      }, (err) => {
        this.alertService.danger(`Error revoking token for user '${this.username}'!`);
        console.error(err);
      }
    );
  }

}
