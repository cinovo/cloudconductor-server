import { Component, EventEmitter, Input, Output } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { Host } from '../util/http/host.http.service';
import { HostsService } from '../util/hosts/hosts.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'home-hoststatus',
  templateUrl: 'home.hoststatus.comp.html'
})
export class HomeHostStatusComponent {

  @Input() hostsObs: Observable<Host[]>;
  @Output() onHostClicked: EventEmitter<string> = new EventEmitter<string>();

  constructor(public hostsService: HostsService) { }

  public hostClicked(host: Host): void {
    this.onHostClicked.emit(host.name);
  }

}
