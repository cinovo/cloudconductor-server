import { Component, ViewEncapsulation, AfterViewInit } from "@angular/core";
import { SettingHttpService, Settings } from "../util/http/setting.http.service";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  moduleId: module.id,
  selector: 'top-navbar',
  styleUrls: ['css/topNav.comp.css'],
  templateUrl: 'html/topNav.comp.html',
  encapsulation: ViewEncapsulation.None
})
export class TopNavComponent implements AfterViewInit {

  protected settings: Settings = {};

  private currentTime: number = Date.now();

  constructor(private settingHttp: SettingHttpService) {
  };

  ngAfterViewInit(): void {
    this.settingHttp.settings.subscribe(
      (result) => this.settings = result
    )
    this.workClock();
  }

  private workClock() {
    this.currentTime = Date.now()
    setTimeout(() => this.workClock(), 1000);
  }

}
