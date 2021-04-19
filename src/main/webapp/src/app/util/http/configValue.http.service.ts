
import {of as observableOf,  BehaviorSubject ,  Observable } from 'rxjs';

import {catchError, map, share, tap} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Validator } from '../validator.util';

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

export interface ConfigDiff {
  service?: string;
  key: string;
  valueA: string;
  valueB: string;
}

@Injectable()
export class ConfigValueHttpService {

  private _templates: BehaviorSubject<string[]> = new BehaviorSubject([]);
  public templates: Observable<string[]> = this._templates.asObservable();
  private reloading = false;

  private _basePathURL = 'api/config';

  constructor(private readonly http: HttpClient) {
    this.reloadTemplates();
  }

  public getValues(template: string): Observable<ConfigValue[]> {
    return this.http.get<ConfigValue[]>(`${this._basePathURL}/clean/unstacked/${template}`);
  }

  public getVariableValues(template?: string): Observable<ConfigValue[]> {
    if (template) {
      return this.http.get<ConfigValue[]>(`${this._basePathURL}/clean/vars/${template}`);
    }
    return this.http.get<ConfigValue[]>(`${this._basePathURL}/clean/vars/null/`);
  }

  public deleteValue(val: ConfigValue): Observable<boolean> {
    let ret;
    if (!Validator.notEmpty(val.service)) {
      ret = this.http.delete<boolean>(`${this._basePathURL}/${val.template}/null/${val.key}`);
    } else {
      ret = this.http.delete<boolean>(`${this._basePathURL}/${val.template}/${val.service}/${val.key}`);
    }

    return ret.do(() => this.reloadTemplates())
  }

  public deleteForTemplate(template: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${template}`).pipe(tap(() => this.reloadTemplates()));
  }

  public deleteForService(template: string, service: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${template}/${service}`);
  }

  public save(val: ConfigValue): Observable<ConfigValue> {
    const ret = this.http.put<ConfigValue>(this._basePathURL, {'@class': 'de.cinovo.cloudconductor.api.model.ConfigValue', ...val}).pipe(share());
    ret.subscribe(() => this.reloadTemplates(), () => {});
    return ret;
  }

  public saveBulk(cvs: ConfigValue[]): Observable<boolean> {
    let elements = [];
    if (cvs) {
      elements = cvs.map(cv => ({"@class": "de.cinovo.cloudconductor.api.model.ConfigValue", ...cv}))
    }
    const cvBody = {'@class': 'de.cinovo.cloudconductor.api.model.ConfigValues', elements};
    return this.http.put<boolean>(`${this._basePathURL}/bulk`, cvBody);
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
    return this.getConfigValueExact(template, service, key).pipe(
      map(s => (s && s.length > 0)),
      catchError(_ => observableOf(false)));
  }

  public getConfigValue(template: string, service: string, key: string): Observable<string> {
    if (!Validator.notEmpty(service)) {
      return this.http.get<string>(`${this._basePathURL}/clean/${template}/null/${key}`);
    }
    return this.http.get<string>(`${this._basePathURL}/clean/${template}/${service}/${key}`);
  }

  public getConfigValueExact(template: string, service: string, key: string): Observable<string> {
    if (!Validator.notEmpty(service)) {
      return this.http.get<string>(`${this._basePathURL}/${template}/null/${key}/exact`);
    }
    return this.http.get<string>(`${this._basePathURL}/${template}/${service}/${key}/exact`);
  }

  public getPreview(template: string, service: string, mode: string): Observable<any> {
    let options;
    if (mode.indexOf('json') > 0) {
      options = {headers: new HttpHeaders({'Accept': mode})};
    } else {
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

  public getDiff(templateA: string, templateB: string): Observable<ConfigDiff[]> {
    return this.http.get<ConfigDiff[]>(`${this._basePathURL}/diff/${templateA}/${templateB}`);
  }

  public migrateGlobalConfig(): Observable<boolean> {
    return this.http.put<boolean>(`${this._basePathURL}/migrate`, {});
  }

}
