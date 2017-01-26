import { Component, ViewEncapsulation, AfterViewInit } from "@angular/core";
import { AdditionalLinkHttpService, AdditionalLink } from "../services/http/additionalLinks.http.service";
import { ConfigValueHttpService } from "../services/http/configValue.http.service";

/// <reference path="../../../typings/globals/jquery/index.d.ts" />;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  moduleId: module.id,
  selector: 'main-navbar',
  styleUrls: ['css/nav.comp.css'],
  templateUrl: 'html/nav.comp.html',
  encapsulation: ViewEncapsulation.None
})
export class NavComponent implements AfterViewInit {

  protected links: Array<AdditionalLink> = [];
  protected templates: Array<string> = [];

  constructor(private linksHttp: AdditionalLinkHttpService,
              private confHttp: ConfigValueHttpService) {
  };

  ngAfterViewInit(): void {
    (<any>$('#sideMenu')).metisMenu();

    this.linksHttp.links.subscribe((result) => this.links = result);
    this.confHttp.templates.subscribe((result) => this.templates = result);

  }
}
