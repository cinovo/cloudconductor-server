import { Component, OnInit, OnDestroy } from '@angular/core';

import { Subscription } from 'rxjs/Subscription';

import { ConfigValueHttpService, ConfigValue } from '../util/http/configValue.http.service';
import { TemplateHttpService } from '../util/http/template.http.service';
import { ServiceHttpService } from '../util/http/service.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cs.preview.comp',
  templateUrl: './cv.preview.comp.html'
})
export class ConfigValuePreview implements OnInit, OnDestroy {

  public templateNames: string[];
  public serviceNames: string[] = [];
  public modes: string[] = ['application/json;charset=UTF-8', 'application/x-javaargs', 'application/x-javaprops'];
  public preview: any;

  private _templateQuery: string;
  private _serviceQuery: string;
  private _modeQuery: string = this.modes[0];

  private _templateNamesSub: Subscription;
  private _serviceNamesSub: Subscription;

  constructor(private configHttp: ConfigValueHttpService,
              private templateHttp: TemplateHttpService,
              private serviceHttp: ServiceHttpService) { };

  ngOnInit(): void {
    this._templateNamesSub = this.configHttp.templates.subscribe((result) => this.templateNames = result);

    this._serviceNamesSub = this.serviceHttp.getServiceNames().subscribe(
      (serviceNames) => this.serviceNames = serviceNames,
      (err) => console.error(err)
    );

    this.loadPreview();
  }

  ngOnDestroy(): void {
    if (this._templateNamesSub) {
      this._templateNamesSub.unsubscribe();
    }

    if (this._serviceNamesSub) {
      this._serviceNamesSub.unsubscribe();
    }
  }

  private loadPreview(): void {
    this.preview = null;
    this.configHttp.getPreview(this.templateQuery, this.serviceQuery, this.modeQuery).map((obj) => {
      if (obj['@class']) {
        delete obj['@class'];
      }

      if (obj instanceof Array) {
        obj = obj.map((ele => {
          delete ele['@class'];
          return ele;
        }));
      }

      return obj;
    }).subscribe(
      (result) => {
        if (result instanceof Array) {
          this.preview = result;
        } else if (result._body) {
          this.preview = result._body;
        }else {
          this.preview = result;
        }

      }
    )
  }

  get templateQuery(): string {
    return this._templateQuery;
  }

  set templateQuery(value: string) {
    this._templateQuery = value;
    this.loadPreview();
  }

  get serviceQuery(): string {
    return this._serviceQuery;
  }

  set serviceQuery(value: string) {
    this._serviceQuery = value;
    this.loadPreview();
  }

  get modeQuery(): string {
    return this._modeQuery;
  }

  set modeQuery(value: string) {
    this.preview = null;
    this._modeQuery = value;
    this.loadPreview();
  }
}
