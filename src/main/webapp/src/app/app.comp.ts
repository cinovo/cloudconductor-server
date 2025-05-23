import { Component, HostListener, AfterViewInit, OnInit, OnDestroy } from '@angular/core';

import { of as observableOf,  Observable, Subscription } from 'rxjs';
import { mergeMap, delay } from 'rxjs/operators';

import { AuthHttpService } from './util/http/auth.http.service';
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
    templateUrl: './app.comp.html',
    standalone: false
})
export class AppComponent implements AfterViewInit, OnInit, OnDestroy {

  private _loginSub: Subscription;
  private _timerSub: Subscription;

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

  constructor(private authTokenProvider: AuthTokenProviderService,
              private authHttp: AuthHttpService) { };

  ngAfterViewInit(): void {
    AppComponent.handleTheme();
  }

  ngOnInit(): void {
    this.loggedIn = this.authTokenProvider.loggedIn;

    this._loginSub = this.loggedIn.subscribe((loggedIn) => {
      if (loggedIn) {
        const d = Math.max(this.authTokenProvider.nextRefresh - (new Date().getTime()), 0);
        this._timerSub = observableOf(this.authTokenProvider.token).pipe(delay(d),
                  mergeMap((token) => this.authHttp.refresh(token)),)
                  .subscribe(
                    (newToken) => this.authTokenProvider.storeToken(newToken),
                    (err) => console.error(err)
                  );
      }
    });
  }

  ngOnDestroy(): void {
    if (this._loginSub) {
      this._loginSub.unsubscribe();
    }

    if (this._timerSub) {
      this._timerSub.unsubscribe();
    }
  }

}
