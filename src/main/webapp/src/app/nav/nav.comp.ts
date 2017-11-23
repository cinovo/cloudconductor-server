import { Component, ViewEncapsulation, AfterViewInit, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { AdditionalLinkHttpService, AdditionalLink } from '../util/http/additionalLinks.http.service';
import { ConfigValueHttpService } from '../util/http/configValue.http.service';

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

  constructor(private linksHttp: AdditionalLinkHttpService,
              private confHttp: ConfigValueHttpService) { };

  ngAfterViewInit(): void {
    (<any>$('#sideMenu')).metisMenu();
  }

  ngOnInit(): void {
    this.links$ = this.linksHttp.links;
    this.templates$ = this.confHttp.templates;
  }
}
