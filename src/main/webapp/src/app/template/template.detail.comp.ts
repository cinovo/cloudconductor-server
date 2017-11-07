import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs/Rx';

import { Heartbeat, WebSocketService } from '../util/websockets/websocket.service';
import { Mode } from '../util/enums.util';
import { TemplateHttpService, Template } from '../util/http/template.http.service';
import { Validator } from '../util/validator.util';
import { WSChangeEvent } from '../util/websockets/ws-change-event.model';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'template-detail',
  templateUrl: './template.detail.comp.html'
})
export class TemplateDetail implements OnInit, OnDestroy {

  private _template: BehaviorSubject<Template> = new BehaviorSubject({name: '', description: ''});
  public template: Observable<Template> = this._template.asObservable();
  public currentTemplate: Template = {name: '', description: '', hosts: []};
  protected autorefesh = false;

  public modes = Mode;

  private _webSocket: Subject<MessageEvent | Heartbeat>;
  private _webSocketSub: Subscription;
  private _heartBeatSub: Subscription;

  constructor(private templateHttp: TemplateHttpService,
              private route: ActivatedRoute,
              private router: Router,
              private wsService: WebSocketService) { };

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const templateName = params['templateName'];

      this.loadTemplate(templateName);
      this.connectWS(templateName);
    });
    this.template.subscribe((result) => this.currentTemplate = result);
  }

  private connectWS(hostName: string): void {
    this.wsService.connect('template', hostName).subscribe((webSocket) => {
      this._webSocket = webSocket;

      this._webSocketSub = this._webSocket.subscribe((event) => {
        const data: WSChangeEvent<Template> = JSON.parse(event.data);

        switch (data.type) {
          case 'UPDATED':
            const updatedTemplate = data.content;
            this._template.next(updatedTemplate);
            break;

          default:
            console.error('Unknown type of WS message!');
            break;
        }
      });

      const iv = (this.wsService.timeout * 0.4);
      this._heartBeatSub = Observable.interval(iv).subscribe(() => {
        this._webSocket.next({ data: 'Alive!' });
      });
    });
  }

  ngOnDestroy(): void {
    this.wsService.disconnect();
    if (this._heartBeatSub) {
      this._heartBeatSub.unsubscribe();
    }
  }

  private loadTemplate(templateName: string) {
    if (Validator.notEmpty(templateName) && templateName !== 'new') {
      this.templateHttp.getTemplate(templateName).subscribe((result) => {
          result.repos.sort();
          this._template.next(result);
        },
        (err) => this.router.navigate(['/not-found', 'template', templateName])
      );
    }
  }

  public reloadTemplate(): void {
    if (this.currentTemplate) {
      this.loadTemplate(this.currentTemplate.name);
    }
  }

}
