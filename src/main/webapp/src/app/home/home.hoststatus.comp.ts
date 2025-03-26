import { Component, EventEmitter, Input, Output, OnInit, OnDestroy } from '@angular/core';

import { Observable ,  Subscription } from 'rxjs';

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
    templateUrl: 'home.hoststatus.comp.html',
    standalone: false
})
export class HomeHostStatusComponent implements OnInit, OnDestroy {

  @Input() hostsObs: Observable<Host[]>;
  @Output() onHostClicked: EventEmitter<string> = new EventEmitter<string>();

  public lastUpdate: number;
  private hostsSub: Subscription;

  constructor(public readonly hostsService: HostsService) { }

  ngOnInit(): void {
    this.hostsSub = this.hostsObs.subscribe(() => {
      this.lastUpdate = new Date().getTime();
    });
  }

  ngOnDestroy(): void {
    if (this.hostsSub) {
      this.hostsSub.unsubscribe();
    }
  }
}
