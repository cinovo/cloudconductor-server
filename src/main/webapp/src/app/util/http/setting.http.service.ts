import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable, BehaviorSubject } from 'rxjs';
import { pluck, share, tap } from 'rxjs/operators';

export interface Settings {
  name?: string;
  description?: string;
  allowautoupdate?: boolean;
  needsApproval?: boolean;
  hostAliveTimer?: number;
  hostAliveTimerUnit?: TimeUnit;
  hostCleanUpTimer?: number;
  hostCleanUpTimerUnit?: TimeUnit;
  indexScanTimer?: number;
  indexScanTimerUnit?: TimeUnit;
  pageRefreshTimer?: number;
  pageRefreshTimerUnit?: string;
  disallowUninstall?: string[];
}

type TimeUnit = 'SECONDS' | 'MINUTES' | 'HOURS' | 'DAYS';

export const timeUnits: {value: TimeUnit, label: string, factor: number}[] = [
  { value: 'SECONDS', label: 'Seconds', factor: 1000 },
  { value: 'MINUTES', label: 'Minutes', factor: 60000 },
  { value: 'HOURS', label: 'Hours', factor: 3600000 },
  { value: 'DAYS', label: 'Days', factor: 86400000 }
];

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Injectable()
export class SettingHttpService {

  private _settings: BehaviorSubject<Settings> = new BehaviorSubject({});
  public settings: Observable<Settings> = this._settings.asObservable();

  private reloading = false;

  private readonly _basePathURL = 'api/settings';

  public static calcIntervalInMillis(n: number, label: string) {
    const unit = timeUnits.find(u => u.value === label);
    if (unit && unit.factor) {
      return n * unit.factor
    }
    return n * 1000;
  }

  constructor(private readonly http: HttpClient) {
    this.reloadSettings();
  }

  get lastInstance(): Settings {
    return this._settings.value;
  }

  public getSettings(): Observable<Settings> {
    return this.http.get<Settings>(this._basePathURL).pipe(
    tap(settings => this._settings.next(settings)),
    share(),);
  }

  public save(settings: Settings): Observable<boolean> {
    settings['@class'] = 'de.cinovo.cloudconductor.api.model.Settings';
    const res = this.http.put<boolean>(this._basePathURL, settings).pipe(share());
    res.subscribe(() => this.reloadSettings(), () => {});
    return res;
  }

  public getNoUninstall(): Observable<string[]> {
    return this.getSettings().pipe(pluck('disallowUninstall'));
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
