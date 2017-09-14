import { Component, AfterViewInit, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, Subject, Subscription, Observable } from 'rxjs';

import { Host, HostHttpService } from '../util/http/host.http.service';
import { AlertService } from '../util/alert/alert.service';
import { Validator } from '../util/validator.util';
import { WebSocketService, Heartbeat } from '../util/websockets/websocket.service';
import { Template } from "../util/http/template.http.service";
import { WSChangeEvent } from "../util/websockets/ws-change-event.model";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'host.detail.comp',
  templateUrl: './host.detail.comp.html'
})
export class HostDetail implements OnInit, OnDestroy {

  private _behavHost: BehaviorSubject<Host> = new BehaviorSubject({name: '', template: ''});
  public obsHost: Observable<Host> = this._behavHost.asObservable();
  public host: Host = {name: '', template: ''};

  private back: {ret: string; id: string};
  private autorefresh = false;

  private _webSocket: Subject<MessageEvent | Heartbeat>;
  private _webSocketSub: Subscription;

  constructor(private route: ActivatedRoute,
              private hostHttp: HostHttpService,
              private alerts: AlertService,
              private wsService: WebSocketService) { };

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const hostName = params['hostName'];

      this.loadData(hostName);
      this.connectWS(hostName);
    });
    this.obsHost.subscribe(
      (result) => this.host = result
    );

    this.route.queryParams.subscribe((params) => {
      this.back = {ret: params['ret'], id: params['id']};
    });
  }

  connectWS(hostName: string): void {
    this.wsService.connect('host', hostName).subscribe((webSocket) => {
      this._webSocket = webSocket;

      this._webSocketSub = this._webSocket.subscribe((event) => {
        const data: WSChangeEvent<Host> = JSON.parse(event.data);

        // TODO handle change events
      });
    });
  }

  ngOnDestroy(): void {
    this.wsService.disconnect();
  }

  private loadData(hostName: string): void {
    if (Validator.notEmpty(hostName) && hostName !== 'new') {
      this.hostHttp.getHost(hostName).subscribe(
        (result) => {
          this._behavHost.next(result);
        },
        (error) => this.alerts.danger('The host does not exist!')
      )
    }
  }

  public reloadHost(): void {
    this.loadData(this.host.name);
  }

}
