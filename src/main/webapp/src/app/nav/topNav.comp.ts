import { AfterContentInit, Component, OnInit, ViewEncapsulation } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { AlertService } from '../util/alert/alert.service';
import { AuthenticationService, User } from '../util/auth/authentication.service';
import { AuthHttpService } from '../util/http/auth.http.service';
import { SettingHttpService, Settings } from '../util/http/setting.http.service';

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

  public userObs: Observable<User>;
  public settingsObs: Observable<Settings>;
  public currentTime: number = Date.now();

  constructor(private settingHttp: SettingHttpService,
              private alertService: AlertService,
              private authService: AuthenticationService,
              private authHttpService: AuthHttpService) { };

  ngOnInit(): void {
    this.settingsObs = this.settingHttp.settings;
    this.userObs = this.authService.currentUser;
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
        this.authService.removeToken();
      }, (err) => {
        this.alertService.danger('Unable to log out!');
        console.error(err);
      }
    );
  }

}
