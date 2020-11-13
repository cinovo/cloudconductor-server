import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import {forkJoin, interval, Observable, Subject, Subscription} from 'rxjs';

import { AlertService } from '../util/alert/alert.service';
import { Sorter } from '../util/sorters.util';
import { SimpleTemplate, Template, TemplateHttpService } from '../util/http/template.http.service';
import { Validator } from '../util/validator.util';
import { Heartbeat, WebSocketService } from '../util/websockets/websocket.service';
import { WSChangeEvent } from '../util/websockets/ws-change-event.model';
import { Repo, RepoHttpService } from '../util/http/repo.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface TemplateGroup {
  group?: string;
  templates: Array<SimpleTemplate>;
  expanded: boolean;
}

@Component({
  selector: 'template-overview',
  templateUrl: './template.overview.comp.html'
})
export class TemplateOverview implements OnInit, OnDestroy {

  private _searchQuery: string = null;
  private _repoQuery: string = null;

  private _webSocket: Subject<MessageEvent | Heartbeat>;

  private _templates: Array<SimpleTemplate> = [];
  public templateTree: Array<TemplateGroup> = [];

  private _webSocketSub: Subscription;
  private _heartBeatSub: Subscription;

  public repos: Repo[];

  public templatesLoaded = false;

  private static filterByRepo(template: SimpleTemplate, repoName: string): boolean {
    if (Validator.notEmpty(repoName)) {
      return template.repos.indexOf(repoName) > -1
    }
    return true;
  }

  private static filterData(template: SimpleTemplate, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return template.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  constructor(private templateHttp: TemplateHttpService,
              private repoHttp: RepoHttpService,
              private router: Router,
              private alerts: AlertService,
              private wsService: WebSocketService) {
  };

  ngOnInit(): void {
    this.loadTemplates();

    this.loadRepos();

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

        this._heartBeatSub = interval(iv).subscribe(() => {
          // send heart beat message via WebSockets
          this._webSocket.next({data: 'Alive!'});
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
    this.templateHttp.getSimpleTemplates().subscribe((result) => {
      this.templates = result;
      this.templatesLoaded = true;
    }, (err) => {
      this.alerts.danger('Error loading templates!');
      this.templatesLoaded = true;
    });
  }

  private loadRepos(): void {
    this.repoHttp.getRepos().subscribe(
      (repos) => this.repos = repos,
      (err) => {
        console.error(err);
      }
    )
  }

  get templates(): SimpleTemplate[] {
    return this._templates;
  }

  set templates(value: SimpleTemplate[]) {
    this._templates = value
      .filter(template => TemplateOverview.filterByRepo(template, this._repoQuery))
      .filter(template => TemplateOverview.filterData(template, this._searchQuery))
      .map(template => {
        template.repos = [...template.repos].sort();
        return template;
      })
      .sort(Sorter.template);

    this.refreshTree();
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadTemplates();
  }

  set repoQuery(value: string) {
    this._repoQuery = value;
    this.loadTemplates();
  }

  public deleteTemplate(template: SimpleTemplate): void {
    this.templateHttp.deleteTemplate(template).subscribe(
      () => {
        // template list is updated via websocket
        this.alerts.success(`Successfully deleted template '${template.name}'.`)
      }, (err) => {
        this.alerts.danger(`Error deleting template '${template.name}'!`);
        console.error(err);
      });
  }

  public deleteAllTemplates(): void {
    const deleteOps: Observable<boolean>[] = this._templates.map(template => this.templateHttp.deleteTemplate(template));

    forkJoin(deleteOps).subscribe(
      () => {
        // template list is updated via websocket
        this.alerts.success(`Successfully deleted ${deleteOps.length} templates.`);
      },
      (err) => {
        this.alerts.danger(`Error deleting all ${deleteOps.length} templates!`)
        console.error(err);
      }
    )
  }

  public gotoDetails(template: Template): void {
    if (template) {
      this.router.navigate(['template', template.name]);
    }
  }

  private refreshTree() {
    this.templateTree = [];
    this._templates.forEach((element) => {
      if (!element.group) {
        element.group = "";
      }
      let te = this.templateTree.find((treeElement) => treeElement.group == element.group);
      if (!te) {
        te = {group: element.group, templates: [], expanded: false};
        this.templateTree.push(te);
      }
      te.templates.push(element);
    });
    this.templateTree.sort(Sorter.groupFieldNoneLast);
    this.templateTree.forEach((e) => e.templates.sort(Sorter.template));
    if (this.templateTree.length == 1) {
      this.templateTree[0].expanded = true;
    }
  }
}
