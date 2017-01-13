/**
 * Created by psigloch on 28.09.2016.
 */
import { Component, HostListener, AfterViewInit } from "@angular/core";
/// <reference path="../../typings/globals/jquery/index.d.ts" />;

@Component({
  moduleId: module.id,
  selector: 'app-component',
  templateUrl: 'html/app.comp.html'
})
export class AppComponent implements AfterViewInit{
  @HostListener('window:resize') onResize() {
    this.handleTheme();
  }

  constructor() {
  };


  public ngAfterViewInit(): void {
    this.handleTheme();
  }

  private handleTheme() {
    let topOffset = 50;
    let width = (window.innerWidth > 0) ? window.innerWidth : screen.width;
    if (width < 768) {
      (<any>$('div.navbar-collapse')).addClass('collapse');
      topOffset = 100; // 2-row-menu
    } else {
      (<any>$('div.navbar-collapse')).removeClass('collapse');
    }

    let height = ((window.innerHeight > 0) ? window.innerHeight : screen.height) - 1;
    height = height - topOffset;
    if (height < 1) height = 1;
    if (height > topOffset) {
      (<any>$("#page-wrapper")).css("min-height", (height) + "px");
    }
  }
}
