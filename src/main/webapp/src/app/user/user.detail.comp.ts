import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { GroupHttpService } from '../util/http/group.http.service';
import { User, UserHttpService } from '../util/http/user.http.service';
import { Mode } from '../util/enums.util';
import { Sorter } from '../util/sorters.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './user.detail.comp.html'
})
export class UserDetailComponent implements OnInit, OnDestroy {

  private _routeSub: Subscription;
  private _userSubject: Subject<User> = new Subject();

  public user$: Observable<User> = this._userSubject.asObservable();
  public mode: Mode;
  public modes = Mode;

  public loginName = '';

  constructor(private userHttp: UserHttpService,
              private route: ActivatedRoute,
              private alertService: AlertService) { }

  ngOnInit(): void {
    this._routeSub = this.route.paramMap.subscribe(paraMap => {
      this.loginName = paraMap.get('loginName');
      if (this.loginName) {
        this.reloadUser();
      } else {
        this.mode = Mode.NEW;
      }
    });
  }

  public reloadUser(): void {
    this.userHttp.getUser(this.loginName).subscribe(
      (user) => {
        user.authTokens.sort((a, b) => Sorter.byFieldDesc(a, b, 'creationDate'));
        this._userSubject.next(user)
      }, (err) => {
        this.alertService.danger(`Error loading user '${this.loginName}'!`);
        console.error(err);
      }
    );
  }

  ngOnDestroy(): void {
    if (this._routeSub) {
      this._routeSub.unsubscribe();
    }
  }

}
