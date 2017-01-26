/**
 * Created by psigloch on 04.11.2016.
 */
/**
 * Created by psigloch on 03.11.2016.
 */
import { Injectable } from "@angular/core";
import { Http } from "@angular/http";
import { HTTPService } from "./abstract.http.service";
import { Validator } from "../../util/validator.util";
import { BehaviorSubject, Observable } from "rxjs";

export interface ConfigValue {
  key: string;
  value: string;
  template: string;
  service?: string;
}

@Injectable()
export class ConfigValueHttpService extends HTTPService {
  private _templates: BehaviorSubject<Array<string>> = new BehaviorSubject([]);
  public templates: Observable<Array<string>> = this._templates.asObservable();
  private reloading: boolean = false;

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'config/';
    this.reloadTemplates();
  }

  public getValues(template: string): Observable<Array<ConfigValue>> {
    return this._get(template + "/unstacked");
  }

  public deleteValue(val: ConfigValue): Observable<boolean> {
    let ret;
    if (!Validator.notEmpty(val.service)) {
      ret = this._delete(val.template + "/null/" + val.key);
    } else {
      ret = this._delete(val.template + "/" + val.service + "/" + val.key);
    }
    ret.subscribe(() => this.reloadTemplates(), () => {
    });
    return ret;
  }

  public save(val: ConfigValue): Observable<ConfigValue> {
    val['@class'] = "de.cinovo.cloudconductor.api.model.ConfigValue";
    let ret = this._put("", val);
    ret.subscribe(() => this.reloadTemplates(), () => {
    });
    return ret;
  }

  private reloadTemplates(): void {
    if (!this.reloading) {
      this.reloading = true;
      this._get("").subscribe(
        (result) => {
          this._templates.next(result.sort((a: string, b: string) => {
            if (a == "GLOBAL") return -1;
            if (b == "GLOBAL") return 1;
            if (a < b) return -1;
            if (a > b) return 1;
            return 0;
          }));
          this.reloading = false;
        }
      );
    }
  }

  public getConfigValue(template: string, service: string, key: string): Observable<string> {
    if (!Validator.notEmpty(service)) {
      return this._get(template + "/null/" + key);
    }
    return this._get(template + "/" + service + "/" + key);
  }
}
