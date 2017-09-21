import { Component, AfterViewInit, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { Sorter } from '../util/sorters.util';
import { TemplateHttpService, Template } from '../util/http/template.http.service';
import { Validator } from '../util/validator.util';
import { WebSocketService, Heartbeat } from '../util/websockets/websocket.service';
import { WSChangeEvent } from '../util/websockets/ws-change-event.model';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'template-overview',
  templateUrl: './template.overview.comp.html'
})
export class TemplateOverview implements OnInit, OnDestroy {

  private _searchQuery: string = null;

  private _webSocket: Subject<MessageEvent | Heartbeat>;

  private _templates: Array<Template> = [];

  private _webSocketSub: Subscription;
  private _heartBeatSub: Subscription;

  private static filterData(template: Template, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return template.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  constructor(private templateHttp: TemplateHttpService,
              private router: Router,
              private alerts: AlertService,
              private wsService: WebSocketService) {  };

  ngOnInit(): void {
    this.loadTemplates();

    this.wsService.connect('templates').subscribe((webSocket) => {
      this._webSocket = webSocket;

      this._webSocketSub = this._webSocket.subscribe((event) => {
        const data: WSChangeEvent<Template> = JSON.parse(event.data);

        let updatedTemplates = this._templates;
        switch (data.type) {
          case 'ADDED':
            updatedTemplates = this._templates.concat(data.content)
            break;

          case 'UPDATED':
            const updatedTemplate = data.content;
            const indexToUpdate = this._templates.findIndex((t) => t.name === updatedTemplate.name);

            updatedTemplates.splice(indexToUpdate, 1, updatedTemplate);
            break;

          case 'DELETED':
            const deletedTemplate = data.content;
            const indexToDelete = this._templates.findIndex((t) => t.name === deletedTemplate.name);

            updatedTemplates.splice(indexToDelete, 1);
            break;

          default:
            console.error('Unknown WS message type!');
            break;
        }

        this.templates = updatedTemplates;
      });

      const iv = (this.wsService.timeout * 0.4);

      this._heartBeatSub = Observable.interval(iv).subscribe(() => {
        // send heart beat message via WebSockets
        this._webSocket.next({ data: 'Alive!' });
      });
    },
    (err) => console.error(err));
  }

  ngOnDestroy(): void {
    if (this._webSocketSub) {
      this._webSocketSub.unsubscribe();
    }

    if (this._heartBeatSub) {
      this._heartBeatSub.unsubscribe();
    }

    this.wsService.disconnect();
  }

  private loadTemplates(): void {
    this.templateHttp.getTemplates().subscribe(
      (result) => this.templates = result
    )
  }

  protected countVersion(versions: any): number {
    return Object.keys(versions).length;
  }

  get templates(): Array<Template> {
    return this._templates;
  }

  set templates(value: Array<Template>) {
    this._templates = value
      .filter(repo => TemplateOverview.filterData(repo, this._searchQuery))
      .sort(Sorter.template);
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadTemplates();
  }

  private deleteTemplate(template: Template): void {
    this.templateHttp.deleteTemplate(template).subscribe(
      () => {
        this._templates.splice(this._templates.indexOf(template), 1);
        this.alerts.success('The template ' + template.name + ' has been deleted.')
      }
    )
  }

  protected deleteAllTemplates(): void {
    for (let template of this._templates) {
      this.deleteTemplate(template);
    }
  }

  protected gotoDetails(template: Template): void {
    if (template) {
      this.router.navigate(['template', template.name]);
    }
  }

}
