import { Injectable } from '@angular/core';

import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { Observer } from 'rxjs/Observer';

import { WSConfigHttpService, WSConfig } from '../http/wsconfig.http.service';

export interface Heartbeat {
  data: string
}

@Injectable()
export class WebSocketService {

  private _baseUrl: string;

  constructor(private wsConfigService: WSConfigHttpService) { }

  public connect(name: string): Observable<Subject<MessageEvent | Heartbeat>> {
    if (this._baseUrl) {
      return Observable.of(this.createWebSocket(this._baseUrl, name));
    } else {
      return this.wsConfigService.getWSConfig()
        .map((config: WSConfig) => {
          this._baseUrl = config.basePath;
          return this.createWebSocket(config.basePath, name);
        });
    }
  }

  private createWebSocket(basePath: string, name: string): Subject<MessageEvent | Heartbeat> {
    const socket = new WebSocket(`${basePath}/${name}`);
    const observable = Observable.create(
      (observer: Observer<MessageEvent>) => {
          socket.onmessage = observer.next.bind(observer);
          socket.onerror = observer.error.bind(observer);
          socket.onclose = observer.complete.bind(observer);
          return socket.close.bind(socket);
      }
    );
    const observer = {
      next: (data: Object) => {
          if (socket.readyState === WebSocket.OPEN) {
              socket.send(JSON.stringify(data));
          }
      }
    };
    return Subject.create(observer, observable);
  }

}
