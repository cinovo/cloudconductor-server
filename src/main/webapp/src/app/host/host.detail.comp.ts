import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { Subscription } from 'rxjs/Subscription';

import { Host, HostHttpService } from '../util/http/host.http.service';
import { AlertService } from '../util/alert/alert.service';
import { Validator } from '../util/validator.util';
import { Heartbeat, WebSocketService } from '../util/websockets/websocket.service';
import { WSChangeEvent } from '../util/websockets/ws-change-event.model';
import { TemplateHttpService } from "../util/http/template.http.service";

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

  private _behavHost: BehaviorSubject<Host> = new BehaviorSubject({name: '', template: '', uuid: ''});
  public obsHost: Observable<Host> = this._behavHost.asObservable();
  public host: Host = {name: '', template: '', uuid: ''};

  public templateChanged: boolean = false;

  private _webSocket: Subject<MessageEvent | Heartbeat>;
  private _webSocketSub: Subscription;
  private _heartBeatSub: Subscription;
  private templates: string[] = [];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private hostHttp: HostHttpService,
              private alerts: AlertService,
              private wsService: WebSocketService,
              private templateHttp: TemplateHttpService) {
  };

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const hostUuid = params['hostUuid'];

      this.loadData(hostUuid);
      this.connectWS(hostUuid);
    });

    this.templateHttp.getTemplateNames().subscribe(
      (result) => {this.templates = result; console.log(result);}
    );

    this.obsHost.subscribe((result) => this.host = result);
  }

  private connectWS(hostUuid: string): void {
    this.wsService.connect('host', hostUuid).subscribe((webSocket) => {
      this._webSocket = webSocket;

      this._webSocketSub = this._webSocket.subscribe((event) => {
        const data: WSChangeEvent<Host> = JSON.parse(event.data);

        switch (data.type) {
          case 'UPDATED':
            const updatedHost = data.content;
            this._behavHost.next(updatedHost);
            break;

          default:
            console.error('Unknown type of WS message!');
            break;
        }
      });

      const iv = (this.wsService.timeout * 0.4);
      this._heartBeatSub = Observable.interval(iv).subscribe(() => {
        // send heart beat message via WebSockets
        this._webSocket.next({data: 'Alive!'});
      });
    });
  }

  ngOnDestroy(): void {
    this.wsService.disconnect();
  }

  private loadData(hostUuid: string): void {
    if (Validator.notEmpty(hostUuid) && hostUuid !== 'new') {
      this.hostHttp.getHost(hostUuid).subscribe(
        (result) => this._behavHost.next(result),
        (error) => this.router.navigate(['/not-found', 'host', hostUuid])
      );
    }
  }

  changeTemplate() {
    if (Validator.notEmpty(this.host.template)) {
      this.hostHttp.moveHost(this.host.uuid, this.host.template).subscribe(
        (result) => this.alerts.success("Moved Host " + this.host.name + " to new Template " + this.host.template),
        (error) => this.alerts.danger("Failed to move Host" + this.host.name + " to new Template " + this.host.template)
      );
    }
  }
}
