import { AfterContentInit, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { AlertService } from '../util/alert/alert.service';
import { AuthHttpService, AuthenticatedUser } from '../util/http/auth.http.service';
import { SettingHttpService, Settings } from '../util/http/setting.http.service';
import { AuthTokenProviderService } from '../util/auth/authtokenprovider.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'top-navbar',
  styleUrls: ['./topNav.comp.scss'],
  templateUrl: './topNav.comp.html',
  encapsulation: ViewEncapsulation.None
})
export class TopNavComponent implements AfterContentInit, OnInit {

  public userObs: Observable<AuthenticatedUser>;
  public settingsObs: Observable<Settings>;
  public currentTime: number = Date.now();

  constructor(private settingHttp: SettingHttpService,
              private alertService: AlertService,
              private authTokenProvider: AuthTokenProviderService,
              private authHttpService: AuthHttpService,
              private router: Router) { };

  ngOnInit(): void {
    this.settingsObs = this.settingHttp.settings;
    this.userObs = this.authTokenProvider.currentUser;
  }

  ngAfterContentInit(): void {
    this.workClock();
  }

  private workClock() {
    this.currentTime = Date.now();
    setTimeout(() => this.workClock(), 1000);
  }

  public logOut() {
    this.authHttpService.logout().subscribe(
      () => {
        this.alertService.success('Successfully logged out.');
        this.authTokenProvider.removeToken();
        this.router.navigate(['/login']);
      }, (err) => {
        this.alertService.danger('Unable to log out!');
        console.error(err);
      }
    );
  }

}
