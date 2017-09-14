import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { Subscription } from 'rxjs/Subscription';

import { Host, HostHttpService } from '../util/http/host.http.service';
import { Sorter } from '../util/sorters.util';
import { Validator } from '../util/validator.util';
import { Template, TemplateHttpService } from '../util/http/template.http.service';
import { WebSocketService, Heartbeat } from '../util/websockets/websocket.service';
import { WSChangeEvent } from '../util/websockets/ws-change-event.model';
import { AlertService } from '../util/alert/alert.service';
/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'host.overview.comp',
  templateUrl: './host.overview.comp.html'
})
export class HostOverview implements OnInit, OnDestroy {

  private _searchQuery: string = null;
  private _searchTemplateQuery: string = null;

  private _hosts: Array<Host> = [];

  private autorefresh = false;

  public templates: Array<Template> = [];
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

  constructor(private alertService: AlertService,
              private hostHttp: HostHttpService,
              private router: Router,
              private templateHttp: TemplateHttpService,
              private wsService: WebSocketService) { };

  ngOnInit(): void {
    this.loadData();

    this.wsService.connect('hosts').subscribe((webSocket) => {
      this._webSocket = webSocket;

      this._webSocketSub = this._webSocket.subscribe((event) => {
        const data: WSChangeEvent<Host> = JSON.parse(event.data);

        let updatedHosts = this._hosts;
        switch (data.type) {
          case 'ADDED':
            // TODO not implemented yet!
            break;

          case 'UPDATED':
            // TODO not implemented yet!
            break;

          case 'DELETED':
            const deletedHost = data.content;
            const indexToDelete = this._hosts.findIndex((h) => h.name === deletedHost.name);

            updatedHosts.splice(indexToDelete, 1);
            break;

          default:
            console.error('Unknown WS message type!');
            break;
        }
        this._hosts = updatedHosts;
      });

      const iv = (this.wsService.timeout * 0.4);
      this._heartBeatSub = Observable.interval(iv).subscribe(() => {
        // send heart beat message via WebSockets
        this._webSocket.next({ data: 'Alive!' });
      });
    });
  }

  ngOnDestroy(): void {
    if (this._heartBeatSub) {
      this._heartBeatSub.unsubscribe();
    }

    this.wsService.disconnect();
  }

  get hosts(): Array<Host> {
    return this._hosts;
  }

  set hosts(value: Array<Host>) {
    this._hosts = value
      .filter(repo => HostOverview.filterData(repo, this._searchQuery))
      .filter(repo => HostOverview.filterTemplateData(repo,  this._searchTemplateQuery))
      .sort(Sorter.host);
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadData();
  }

  get searchTemplateQuery(): string {
    return this._searchTemplateQuery;
  }

  set searchTemplateQuery(value: string) {
    this._searchTemplateQuery = value;
    this.loadData();
  }

  private loadData() {
    this.hostHttp.getHosts().subscribe(
      (result) => this.hosts = result
    );

    this.templateHttp.getTemplates().subscribe(
      (result) => this.templates = result
    );
  }

  public reloadHosts(): void {
    this.loadData();
  }

  protected gotoDetails(host: Host) {
    this.router.navigate(['host', host.name]);
  }

  protected isAlive(host: Host): boolean {
    let now = new Date();
    return now.getMilliseconds() - host.lastSeen < 30 * 1000 * 60;
  }

  public deleteHosts() {
    this._hosts.forEach(h => this.deleteHost(h));
  }

  public deleteHost(hostToDelete: Host) {
    this.hostHttp.deleteHost(hostToDelete).subscribe(() => {
      this.alertService.success(`Successfully deleted host ${hostToDelete.name}!`);
    },
    (err) => {
      this.alertService.danger(`An error occured deleting host '${hostToDelete.name}'!`);
      console.error(err);
    });
  }

}
