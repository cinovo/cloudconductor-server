import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { interval, Subject, Subscription } from 'rxjs';

import { HostsService } from '../util/hosts/hosts.service';
import { Host, HostHttpService } from '../util/http/host.http.service';
import { Sorter } from '../util/sorters.util';
import { Validator } from '../util/validator.util';
import { Heartbeat, WebSocketService } from '../util/websockets/websocket.service';
import { WSChangeEvent } from '../util/websockets/ws-change-event.model';
import { AlertService } from '../util/alert/alert.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
    selector: 'host-overview',
    templateUrl: './host.overview.comp.html',
    standalone: false
})
export class HostOverview implements OnInit, OnDestroy {

  private _searchQuery: string = null;
  private _searchTemplateQuery: string = null;

  private _hosts: Host[] = [];

  public hostsLoaded = false;
  public templates: string[] = [];
  private _webSocket: Subject<MessageEvent | Heartbeat>;

  private _webSocketSub: Subscription;
  private _heartBeatSub: Subscription;

  private static filterData(host: Host, query: string) {
    if (Validator.notEmpty(query)) {
      let res = host.name.indexOf(query.trim()) >= 0;
      if (host.agent && !res) {
        res = host.agent.indexOf(query.trim()) >= 0;
      }
      return res;
    }
    return true;
  }

  private static filterTemplateData(host: Host, templateQuery: string) {
    if (Validator.notEmpty(templateQuery)) {
      return host.template === templateQuery;
    }
    return true;
  }

  constructor(private readonly alertService: AlertService,
              private readonly hostHttp: HostHttpService,
              public readonly hostsService: HostsService,
              private readonly router: Router,
              private readonly wsService: WebSocketService) { }

  ngOnInit(): void {
    this.loadData();

    this.wsService.connect('hosts').subscribe((webSocket) => {
      this._webSocket = webSocket;

      this._webSocketSub = this._webSocket.subscribe((event) => {
        const data: WSChangeEvent<Host> = JSON.parse(event.data);

        let updatedHosts = this._hosts.slice();
        switch (data.type) {
          case 'ADDED':
            updatedHosts = updatedHosts.concat(data.content);
            break;

          case 'UPDATED':
            const updatedHost = data.content;
            const indexToUpdate = updatedHosts.findIndex((h) => h.uuid === updatedHost.uuid);
            if (indexToUpdate > -1){
              updatedHosts.splice(indexToUpdate, 1, updatedHost);
            }
            break;

          case 'DELETED':
            const deletedHost = data.content;
            const indexToDelete = updatedHosts.findIndex((h) => h.uuid === deletedHost.uuid);

            if (indexToDelete > -1) {
              updatedHosts.splice(indexToDelete, 1);
            }
            break;

          default:
            console.error('Unknown WS message type!');
            break;
        }
        this.hosts = updatedHosts;
      });

      const iv = (this.wsService.timeout * 0.4);
      this._heartBeatSub = interval(iv).subscribe(() => {
        // send heart beat message via WebSockets
        this._webSocket.next({data: 'Alive!'});
      });
    });
  }

  ngOnDestroy(): void {
    this._heartBeatSub?.unsubscribe();
    this._webSocketSub?.unsubscribe();
    this.wsService.disconnect();
  }

  get hosts(): Host[] {
    return this._hosts;
  }

  set hosts(value: Host[]) {
    this._hosts = value
      .filter(host => HostOverview.filterData(host, this._searchQuery))
      .filter(host => HostOverview.filterTemplateData(host, this._searchTemplateQuery))
      .sort(Sorter.host);

    this._hosts.forEach((h) => this.templates.indexOf(h.template) === -1 ? this.templates.push(h.template) : null);
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadData();
  }

  set searchTemplateQuery(value: string) {
    this._searchTemplateQuery = value;
    this.loadData();
  }

  private loadData() {
    this.hostHttp.getSimpleHosts().subscribe((result) => {
      this.hosts = result;
      this.hostsLoaded = true;
    }, (err) => {
      this.alertService.danger('Error loading hosts!');
      console.error(err);
      this.hostsLoaded = true;
    });
  }
  public gotoDetails(host: Host): Promise<boolean> {
    return this.router.navigate(['host', host.uuid]);
  }

  public deleteHosts() {
    this._hosts.forEach(h => this.deleteHost(h));
  }

  public deleteHost(hostToDelete: Host) {
    this.hostHttp.deleteHost(hostToDelete).subscribe(() => {
        this.alertService.success(`Successfully deleted host ${hostToDelete.name}!`);
      },
      (err) => {
        this.alertService.danger(`An error occurred deleting host '${hostToDelete.name}'!`);
        console.error(err);
      });
  }
}
