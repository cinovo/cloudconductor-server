import { Component, HostListener, AfterViewInit, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable'

import { AuthTokenProviderService } from './util/auth/authtokenprovider.service';

declare let $: any;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'app-component',
  templateUrl: './app.comp.html'
})
export class AppComponent implements AfterViewInit, OnInit {

  public loggedIn: Observable<boolean>;

  private static handleTheme() {
    let topOffset = 50;
    let width = (window.innerWidth > 0) ? window.innerWidth : screen.width;
    if (width < 768) {
      (<any>$('div.navbar-collapse')).addClass('collapse');
      topOffset = 100; // 2-row-menu
    } else {
      (<any>$('div.navbar-collapse')).removeClass('collapse');
    }

    let height = ((window.innerHeight > 0) ? window.innerHeight : screen.height) - 20;
    height = height - topOffset;
    if (height < 1) {
      height = 1;
    }
    if (height > topOffset) {
      (<any>$('#page-wrapper')).css('min-height', (height) + 'px');
    }
  }

  @HostListener('window:resize') onResize() {
    AppComponent.handleTheme();
  }

  constructor(private authTokenProvider: AuthTokenProviderService) { };

  ngAfterViewInit(): void {
    AppComponent.handleTheme();
  }

  ngOnInit(): void {
    this.loggedIn = this.authTokenProvider.loggedIn;
  }

}
