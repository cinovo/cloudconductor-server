import { Location } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { GroupHttpService } from '../util/http/group.http.service';
import { User, UserHttpService } from '../util/http/user.http.service';
import { Mode } from '../util/enums.util';

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

  public user$: Observable<User>;
  public mode: Mode;
  public modes = Mode;

  private _routeSub: Subscription;

  public newGroupForm: FormGroup;

  constructor(private userHttp: UserHttpService,
              private route: ActivatedRoute,
              private fb: FormBuilder,
              private location: Location,
              private alertService: AlertService) {
    this.newGroupForm = fb.group({
      'newGroup': ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this._routeSub = this.route.paramMap.subscribe(paraMap => {
      const loginName = paraMap.get('loginName');
      if (loginName) {
        this.user$ = this.userHttp.getUser(loginName);
      } else {
        this.mode = Mode.NEW;
      }
    });
  }

  ngOnDestroy(): void {
    if (this._routeSub) {
      this._routeSub.unsubscribe();
    }
  }

}
