import { Component, AfterViewInit, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, Subject, Subscription, Observable } from 'rxjs';

import { Host, HostHttpService } from '../util/http/host.http.service';
import { AlertService } from '../util/alert/alert.service';
import { Validator } from '../util/validator.util';
import { WebSocketService, Heartbeat } from '../util/websockets/websocket.service';
import { Template } from '../util/http/template.http.service';
import { WSChangeEvent } from '../util/websockets/ws-change-event.model';

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

  private autorefresh = false;

  private _webSocket: Subject<MessageEvent | Heartbeat>;
  private _webSocketSub: Subscription;
  private _heartBeatSub: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private hostHttp: HostHttpService,
              private alerts: AlertService,
              private wsService: WebSocketService) { };

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const hostName = params['hostName'];

      this.loadData(hostName);
      this.connectWS(hostName);
    });
    this.obsHost.subscribe((result) => this.host = result);
  }

  private connectWS(hostName: string): void {
    this.wsService.connect('host', hostName).subscribe((webSocket) => {
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
        this._webSocket.next({ data: 'Alive!' });
      });
    });
  }

  ngOnDestroy(): void {
    this.wsService.disconnect();
  }

  private loadData(hostName: string): void {
    if (Validator.notEmpty(hostName) && hostName !== 'new') {
      this.hostHttp.getHost(hostName).subscribe(
        (result) => this._behavHost.next(result),
        (error) => this.router.navigate(['/not-found', 'host', hostName]));
    }
  }

}
