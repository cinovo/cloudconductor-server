import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';

import { AuthenticationService } from '../auth/authentication.service';
import { HTTPService } from './abstract.http.service';
import { Validator } from '../../util/validator.util';

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
export class ConfigValueHttpService extends HTTPService {
  private _templates: BehaviorSubject<Array<string>> = new BehaviorSubject([]);
  public templates: Observable<Array<string>> = this._templates.asObservable();
  private reloading = false;

  constructor(protected http: Http,
              protected authService: AuthenticationService) {
    super(http, authService);
    this.basePathURL = 'config/';
    this.reloadTemplates();
  }

  public getValues(template: string): Observable<ConfigValue[]> {
    return this._get(template + '/unstacked');
  }

  public deleteValue(val: ConfigValue): Observable<boolean> {
    let ret;
    if (!Validator.notEmpty(val.service)) {
      ret = this._delete(val.template + '/null/' + val.key);
    } else {
      ret = this._delete(val.template + '/' + val.service + '/' + val.key);
    }
    ret.subscribe(() => this.reloadTemplates(), () => {
    });
    return ret;
  }

  public save(val: ConfigValue): Observable<ConfigValue> {
    val['@class'] = 'de.cinovo.cloudconductor.api.model.ConfigValue';
    let ret = this._put('', val);
    ret.subscribe(() => this.reloadTemplates(), () => {
    });
    return ret;
  }

  /* tslint:disable:curly */
  private reloadTemplates(): void {
    if (!this.reloading) {
      this.reloading = true;
      this._get('').subscribe(
        (result) => {
          this._templates.next(result.sort((a: string, b: string) => {
            if (a === 'GLOBAL') return -1;
            if (b === 'GLOBAL') return 1;
            if (a < b) return -1;
            if (a > b) return 1;
            return 0;
          }));
          this.reloading = false;
        }
      );
    }
  }

  public existsConfigValue(template: string, service: string, key: string): Observable<boolean> {
    return this.getConfigValue(template, service, key)
                .map(s => (s && s.length > 0))
                .catch(err => Observable.of(false));
  }

  public getConfigValue(template: string, service: string, key: string): Observable<string> {
    if (!Validator.notEmpty(service)) {
      return this._get(template + '/null/' + key);
    }
    return this._get(template + '/' + service + '/' + key);
  }

  public getPreview(template: string, service: string, mode: string): Observable<any> {
    const additionalheaders: Headers = new Headers(
      {'Accept': mode}
    );

    let preview$: Observable<any>;
    if (Validator.notEmpty(service)) {
      preview$ = this._get(template + '/' + service, additionalheaders);
    } else {
      preview$ = this._get(template, additionalheaders);
    }

    return preview$;
  }
}
