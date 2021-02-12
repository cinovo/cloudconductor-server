import { Injectable } from '@angular/core';

import { Host } from '../http/host.http.service';
import { SettingHttpService } from '../http/setting.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class HostsService {

  private static readonly defaultHostsInterval = 30 * 1000 * 60;

  constructor(private readonly settingHttpService: SettingHttpService) { }

  public isAlive(host: Host): boolean {
    const settings = this.settingHttpService.lastInstance;

    let hostsInterval: number;
    if (settings) {
      hostsInterval = SettingHttpService.calcIntervalInMillis(settings.hostAliveTimer, settings.hostAliveTimerUnit);
    } else {
      hostsInterval = HostsService.defaultHostsInterval
    }

    return (new Date().getTime() - host.lastSeen) < hostsInterval;
  }

}
