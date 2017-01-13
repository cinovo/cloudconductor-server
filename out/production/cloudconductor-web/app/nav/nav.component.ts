import { Component, ViewEncapsulation, AfterViewInit } from "@angular/core";
import { Router } from "@angular/router";

/// <reference path="../../../typings/globals/jquery/index.d.ts" />;

@Component({
  moduleId: module.id,
  selector: 'main-navbar',
  styleUrls: ['css/nav.component.css'],
  templateUrl: 'html/nav.component.html',
  encapsulation: ViewEncapsulation.None
})
export class NavComponent  implements AfterViewInit {

  constructor(private router: Router) {

  };

  ngAfterViewInit(): void {
    (<any>$('#sideMenu')).metisMenu();
  }
}
