import { Component, ViewEncapsulation, AfterViewInit } from "@angular/core";
import { SettingHttpService, Settings } from "../services/http/setting.http.service";

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
  constructor(private settingHttp: SettingHttpService) {
  };

  ngAfterViewInit(): void {
    this.settingHttp.settings.subscribe(
      (result) => this.settings = result
    )
  }

}
