/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { Component, AfterViewInit } from "@angular/core";
import { ConfigValueHttpService, ConfigValue } from "../util/http/configValue.http.service";
import { TemplateHttpService } from "../util/http/template.http.service";
import { ServiceHttpService } from "../util/http/service.http.service";

@Component({
  selector: 'cs.preview.comp',
  templateUrl: './cv.preview.comp.html'
})
export class ConfigValuePreview implements AfterViewInit {

  private templates: Array<string> = [];
  private services: Array<string> = [];
  private modes: Array<string> = ['application/json;charset=UTF-8', 'application/x-javaargs', 'application/x-javaprops'];


  private _templateQuery: string;
  private _serviceQuery: string;
  private _modeQuery: string = this.modes[0];
  private preview: any;

  constructor(private configHttp: ConfigValueHttpService,
              private templateHttp: TemplateHttpService,
              private serviceHttp: ServiceHttpService) {
  };

  ngAfterViewInit(): void {
    this.templateHttp.getTemplates().subscribe(
      (result) => {
        for (let template of result) {
          this.templates.push(template.name);
        }
      }
    );

    this.serviceHttp.getServices().subscribe(
      (result) => {
        for (let service of result) {
          if (this.services.indexOf(service.name) < 0) {
            this.services.push(service.name);
          }
        }
      }
    );
    this.loadPreview();
  }

  private loadPreview(): void {
    this.preview = null;
    this.configHttp.getPreview(this.templateQuery, this.serviceQuery, this.modeQuery).subscribe(
      (result) => {
        if(result instanceof Array) {
          this.preview = result;
        }else if(result._body) {
          this.preview = result._body;
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
