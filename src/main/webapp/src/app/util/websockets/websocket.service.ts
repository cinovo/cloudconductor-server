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
  private _timeout: number;
  private _socket: WebSocket;

  constructor(private wsConfigService: WSConfigHttpService) { }

  get timeout() {
    return this._timeout;
  }

  public connect(name: string, objName = ''): Observable<Subject<MessageEvent | Heartbeat>> {
    if (this._baseUrl) {
      return Observable.of(this.createWebSocket(this._baseUrl, name, objName));
    } else {
      return this.wsConfigService.getWSConfig()
        .map((config: WSConfig) => {
          this._baseUrl = config.basePath;
          this._timeout = config.timeout;
          return this.createWebSocket(config.basePath, name, objName);
        });
    }
  }

  private createWebSocket(basePath: string, name: string, pName: string): Subject<MessageEvent | Heartbeat> {
    let url = `${basePath}/${name}`;
    if (pName.length > 0) {
      url += `?name=${pName}`;
    }
    const socket = new WebSocket(url);
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
    this._socket = socket;
    return Subject.create(observer, observable);
  }

  public disconnect() {
    if (this._socket) {
      this._socket.close();
    }
  }

}
