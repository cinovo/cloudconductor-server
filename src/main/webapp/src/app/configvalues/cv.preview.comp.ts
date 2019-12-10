import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from "@angular/router";

import { Subscription } from 'rxjs/Subscription';

import { ConfigValueHttpService } from '../util/http/configValue.http.service';
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
  public modes = ['application/json;charset=UTF-8', 'application/x-javaargs', 'application/x-javaprops'];
  public preview: any;

  private _templateQuery: string;
  private _serviceQuery: string;
  private _modeQuery: string = this.modes[0];

  private _templateNamesSub: Subscription;
  private _serviceNamesSub: Subscription;

  constructor(private configHttp: ConfigValueHttpService,
              private templateHttp: TemplateHttpService,
              private serviceHttp: ServiceHttpService,
              private route: ActivatedRoute) {
  };

  ngOnInit(): void {
    this._templateNamesSub = this.configHttp.templates.subscribe((result) => this.templateNames = result);

    this._serviceNamesSub = this.serviceHttp.getServiceNames().subscribe(
      (serviceNames) => this.serviceNames = serviceNames,
      (err) => console.error(err)
    );

    this.route.paramMap.subscribe((paramMap) => {
      this._templateQuery = paramMap.get('template');
      this.loadPreview();
    });
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
    this.configHttp.getPreview(this.templateQuery, this.serviceQuery, this.modeQuery)
      .map(ConfigValuePreview.cleanObjects)
      .subscribe((result) => {
        if (result instanceof Array) {
          this.preview = result;
        } else if (result._body) {
          this.preview = result._body;
        } else {
          this.preview = result;
        }
      }
    );
  }

  private static cleanObjects(obj: any): any {
    if (obj instanceof Array) {
      return obj.map(ConfigValuePreview.cleanObject);
    }
    return ConfigValuePreview.cleanObject(obj);
  }

  private static cleanObject(obj: any): any {
    if (obj && obj instanceof Object && obj.hasOwnProperty("@class")) {
      const {["@class"]: _, ...cleanObj} = obj;
      return cleanObj
    }
    return obj;
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
