import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';

import { Validator } from '../../util/validator.util';
import { HttpParams } from "@angular/common/http/src/params";
import { HttpResponse } from "@angular/common/http/src/response";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface ConfigValue {
  key: string;
  value: string;
  template: string;
  service?: string;
}

@Injectable()
export class ConfigValueHttpService {

  private _templates: BehaviorSubject<string[]> = new BehaviorSubject([]);
  public templates: Observable<string[]> = this._templates.asObservable();
  private reloading = false;

  private _basePathURL = 'api/config';

  constructor(private http: HttpClient) {
    this.reloadTemplates();
  }

  public getValues(template: string): Observable<ConfigValue[]> {
    return this.http.get<ConfigValue[]>(`${this._basePathURL}/${template}/unstacked`);
  }

  public deleteValue(val: ConfigValue): Observable<boolean> {
    let ret;
    if (!Validator.notEmpty(val.service)) {
      ret = this.http.delete<boolean>(`${this._basePathURL}/${val.template}/null/${val.key}`);
    } else {
      ret = this.http.delete<boolean>(`${this._basePathURL}/${val.template}/${val.service}/${val.key}`);
    }

    ret.subscribe(
      () => this.reloadTemplates(),
      (err) => console.error(err)
    );
    return ret;
  }

  public save(val: ConfigValue): Observable<ConfigValue> {
    val['@class'] = 'de.cinovo.cloudconductor.api.model.ConfigValue';
    let ret = this.http.put<ConfigValue>(this._basePathURL, val);
    ret.subscribe(() => this.reloadTemplates(), () => { });
    return ret;
  }

  /* tslint:disable:curly */
  private reloadTemplates(): void {
    if (!this.reloading) {
      this.reloading = true;
      this.http.get<string[]>(this._basePathURL).subscribe(
        (result) => {
          this._templates.next(result.sort((a: string, b: string) => {
            if (a === 'GLOBAL') return -1;
            if (b === 'GLOBAL') return 1;
            if (a < b) return -1;
            if (a > b) return 1;
            return 0;
          }));
          this.reloading = false;
        }, (err) => {
          console.error(err);
        }
      );
    }
  }

  public existsConfigValue(template: string, service: string, key: string): Observable<boolean> {
    return this.getConfigValueExact(template, service, key)
                .map(s => (s && s.length > 0))
                .catch(err => Observable.of(false));
  }

  public getConfigValue(template: string, service: string, key: string): Observable<string> {
    if (!Validator.notEmpty(service)) {
      return this.http.get<string>(`${this._basePathURL}/${template}/null/${key}`);
    }
    return this.http.get<string>(`${this._basePathURL}/${template}/${service}/${key}`);
  }

  public getConfigValueExact(template: string, service: string, key: string): Observable<string> {
    if (!Validator.notEmpty(service)) {
      return this.http.get<string>(`${this._basePathURL}/${template}/null/${key}/exact`);
    }
    return this.http.get<string>(`${this._basePathURL}/${template}/${service}/${key}/exact`);
  }

  public getPreview(template: string, service: string, mode: string): Observable<any> {
    let options ={};
    if(mode.indexOf("json")>0) {
       options = {headers: new HttpHeaders({'Accept': mode})};
    }else {
       options = {headers: new HttpHeaders({'Accept': mode}), responseType: 'text' as 'text'};
    }


    let preview$: Observable<any>;
    if (Validator.notEmpty(service)) {
      preview$ = this.http.get(`${this._basePathURL}/${template}/${service}`, options);
    } else {
      preview$ = this.http.get(`${this._basePathURL}/${template}`, options);
    }

    return preview$;
  }
}
