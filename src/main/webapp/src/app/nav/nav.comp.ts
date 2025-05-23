import { AfterViewInit, Component, OnInit, ViewEncapsulation } from '@angular/core';

import { Observable } from 'rxjs';

import { AdditionalLink, AdditionalLinkHttpService } from '../util/http/additionalLinks.http.service';

declare let $: any;

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
    encapsulation: ViewEncapsulation.None,
    standalone: false
})
export class NavComponent implements AfterViewInit, OnInit {

  public links$: Observable<AdditionalLink[]>;
  public templates$: Observable<string[]>;

  constructor(private readonly linksHttp: AdditionalLinkHttpService) {};

  ngAfterViewInit(): void {
    ($('#sideMenu') as any).metisMenu();
  }

  ngOnInit(): void {
    this.links$ = this.linksHttp.links;
  }
}
