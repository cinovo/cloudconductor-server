/**
 * Created by psigloch on 04.11.2016.
 */
/**
 * Created by psigloch on 03.11.2016.
 */
import { Injectable } from "@angular/core";
import { Http } from "@angular/http";
import { Observable } from "rxjs/Observable";
import { HTTPService } from "./abstract.http.service";
import { Validator } from "../../util/validator.util";

export interface ConfigValue {
  key: string;
  value: string;
  template: string;
  service?: string;
}

@Injectable()
export class ConfigValueHttpService extends HTTPService {
  private _templates: Array<string> = [];

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'config/';
  }

  public getValues(template: string): Observable<Array<ConfigValue>> {
    return this._get(template + "/unstacked");
  }

  public deleteValue(val: ConfigValue): Observable<boolean> {
    if (!Validator.notEmpty(val.service)) {
      return this._delete(val.template + "/null/" + val.key);
    }
    return this._delete(val.template + "/" + val.service + "/" + val.key);
  }

  public save(val: ConfigValue): Observable<ConfigValue> {
    val['@class'] = "de.cinovo.cloudconductor.api.model.ConfigValue";
    return this._put("", val);
  }

  get templates(): Array<string> {
    if (!this._templates || this._templates.length < 1) {
      this.reloadTemplates();
    }
    return this._templates;
  }

  set templates(val: Array<string>) {
    this._templates = val.sort((a: string, b: string) => {
      if (a == "GLOBAL") return -1;
      if (b == "GLOBAL") return 1;
      if (a < b) return -1;
      if (a > b) return 1;
      return 0;
    });
  }

  public reloadTemplates(): void {
    this._get("").subscribe((result) => this.templates = result);
  }

  public getConfigValue(template: string, service: string, key: string): Observable<string> {
    if(!Validator.notEmpty(service)) {
      return this._get(template + "/null/" + key);
    }
    return this._get(template + "/" + service + "/" + key);
  }
}
