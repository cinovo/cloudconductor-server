import { Injectable } from '@angular/core';

import { Host } from '../http/host.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class HostsService {

  private hostsInterval = 30 * 1000 * 60;

  constructor() { }

  public isAlive(host: Host): boolean {
    return new Date().getTime() - host.lastSeen < this.hostsInterval;
  }

}
