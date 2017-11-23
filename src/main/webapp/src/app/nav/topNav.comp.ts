import { AfterContentInit, Component, OnInit, ViewEncapsulation } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { SettingHttpService, Settings } from '../util/http/setting.http.service';
import { AuthenticationService } from '../util/auth/authentication.service';

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

  public settingsObs: Observable<Settings>;
  public currentTime: number = Date.now();

  constructor(private settingHttp: SettingHttpService,
              private authService: AuthenticationService) { };

  ngOnInit(): void {
    this.settingsObs = this.settingHttp.settings;
  }

  ngAfterContentInit(): void {
    this.workClock();
  }

  private workClock() {
    this.currentTime = Date.now()
    setTimeout(() => this.workClock(), 1000);
  }

  public logOut() {
    this.authService.logout();
  }

}
