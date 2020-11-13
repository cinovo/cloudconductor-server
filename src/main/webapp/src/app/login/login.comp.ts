import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Subscription } from 'rxjs';

import { AlertService } from '../util/alert/alert.service';
import { AuthTokenProviderService } from '../util/auth/authtokenprovider.service';
import { AuthHttpService, Authentication } from '../util/http/auth.http.service';
import {map, take} from 'rxjs/operators';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './login.comp.html',
  styleUrls: ['./login.comp.scss']
})
export class LoginComponent implements OnInit, OnDestroy {

  public loginForm: FormGroup;

  private _redirect = '/home';
  private _querySub: Subscription;

  constructor(private fb: FormBuilder,
    private authHttp: AuthHttpService,
    private authTokenProvider: AuthTokenProviderService,
    private alertService: AlertService,
    private route: ActivatedRoute,
    private router: Router) {
    this.loginForm = fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this._querySub = this.route.queryParamMap.subscribe(
      (queryParaMap) => {
        const redirect = queryParaMap.get('redirect');
        if (redirect) {
          this._redirect = redirect;
        }
      }
    );
  }

  ngOnDestroy(): void {
    if (this._querySub) {
      this._querySub.unsubscribe();
    }
  }

  public login(auth: Authentication): void {
    this.authHttp.login(auth).pipe(
        map((jwt: string) => this.authTokenProvider.storeToken(jwt)),
        take(1)
      ).subscribe(
      (user) => {
        if (!AuthTokenProviderService.isAnonymous(user)) {
          this.router.navigate([this._redirect]);
        } else {
          this.alertService.danger('Authentication failed! You logged in as anonymous...');
        }
        this.loginForm.reset();
      }, (err) => {
        this.alertService.danger('Authentication failed!');
        this.loginForm.reset();
      }
    );
  }

}
