import { Component, ViewEncapsulation, AfterViewInit, AfterContentInit } from '@angular/core';

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
export class TopNavComponent implements AfterViewInit, AfterContentInit {

  public settings: Settings = {};
  public currentTime: number = Date.now();

  constructor(private settingHttp: SettingHttpService) { };

  ngAfterViewInit(): void {
    this.settingHttp.settings.subscribe(
      (result) => this.settings = result
    );
  }

  ngAfterContentInit(): void {
    this.workClock();
  }

  private workClock() {
    this.currentTime = Date.now()
    setTimeout(() => this.workClock(), 1000);
  }

}
