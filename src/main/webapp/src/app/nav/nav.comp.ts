import { AfterViewInit, Component, OnInit, ViewEncapsulation } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { AdditionalLink, AdditionalLinkHttpService } from '../util/http/additionalLinks.http.service';

declare let $: any;

/// <reference path="../../../typings/globals/jquery/index.d.ts" />;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'main-navbar',
  styleUrls: ['./nav.comp.scss'],
  templateUrl: './nav.comp.html',
  encapsulation: ViewEncapsulation.None
})
export class NavComponent implements AfterViewInit, OnInit {

  public links$: Observable<AdditionalLink[]>;
  public templates$: Observable<string[]>;

  constructor(private linksHttp: AdditionalLinkHttpService) {
  };

  ngAfterViewInit(): void {
    (<any>$('#sideMenu')).metisMenu();
  }

  ngOnInit(): void {
    this.links$ = this.linksHttp.links;
  }
}
