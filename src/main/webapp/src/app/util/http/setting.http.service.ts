import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable, BehaviorSubject } from 'rxjs';

import { HTTPService } from './abstract.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface Settings {
  name?: string;
  description?: string;
  allowautoupdate?: boolean;
  needsApproval?: boolean;
  hostCleanUpTimer?: number;
  hostCleanUpTimerUnit?: string;
  indexScanTimer?: number;
  indexScanTimerUnit?: string;
  pageRefreshTimer?: number;
  pageRefreshTimerUnit?: string;
  disallowUninstall?: string[];
}

@Injectable()
export class SettingHttpService extends HTTPService {

  private _settings: BehaviorSubject<Settings> = new BehaviorSubject({});
  public settings: Observable<Settings> = this._settings.asObservable();

  private reloading = false;

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'settings/';
    this.reloadSettings();
  }

  public getSettings(): Observable<Settings> {
    return this._get('');
  }

  public save(settings: Settings): Observable<boolean> {
    settings['@class'] = 'de.cinovo.cloudconductor.api.model.Settings';
    let res = this._put('', settings).share();
    res.subscribe(() => this.reloadSettings(), () => {});
    return res;
  }

  public getNoUninstall(): Observable<string[]> {
    return this.getSettings().pluck('disallowUninstall');
  }

  private reloadSettings() {
    if (!this.reloading) {
      this.reloading = true;
      this.getSettings().subscribe(
        (result) => {
          result.disallowUninstall.sort();
          this._settings.next(result);
          this.reloading = false;
        }
      );
    }
  }
}
