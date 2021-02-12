import { Injectable } from '@angular/core';

import { Repo } from '../http/repo.http.service';
import { SettingHttpService } from '../http/setting.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class RepoScansService {

  constructor(private readonly settingHttpService: SettingHttpService) { }

  private get scanInterval() {
    const intervalNumber = this.settingHttpService.lastInstance.indexScanTimer;
    const intervalUnit = this.settingHttpService.lastInstance.indexScanTimerUnit;
    return SettingHttpService.calcIntervalInMillis(intervalNumber, intervalUnit);
  }

  public isDue(repo: Repo): boolean {
    const diff = new Date().getTime() - repo.lastIndex;
    return diff > this.scanInterval;
  }

  public isPastDue(repo: Repo): boolean {
    const diff = new Date().getTime() - repo.lastIndex;
    return diff > 2 * this.scanInterval;
  }

}
