import { Component, ViewEncapsulation, AfterViewInit } from "@angular/core";
import { Router } from "@angular/router";
import { AdditionalLinkHttpService, AdditionalLink } from "../services/http/additionalLinks.http.service";
import { ConfigValueHttpService } from "../services/http/configValue.http.service";

/// <reference path="../../../typings/globals/jquery/index.d.ts" />;

@Component({
  moduleId: module.id,
  selector: 'main-navbar',
  styleUrls: ['css/nav.comp.css'],
  templateUrl: 'html/nav.comp.html',
  encapsulation: ViewEncapsulation.None
})
export class NavComponent  implements AfterViewInit {

  private links:Array<AdditionalLink> = [];

  constructor(private router: Router, private linksHttp:AdditionalLinkHttpService, private confHttp:ConfigValueHttpService) {

  };

  ngAfterViewInit(): void {
    (<any>$('#sideMenu')).metisMenu();

    this.linksHttp.getLinks().subscribe((result) => this.links = result);

  }
}
