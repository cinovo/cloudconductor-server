import { Component, AfterViewInit } from "@angular/core";
import { Host, HostHttpService } from "../util/http/host.http.service";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";
import { Router } from "@angular/router";
import { Template, TemplateHttpService } from "../util/http/template.http.service";
/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  moduleId: module.id,
  selector: 'host.overview.comp',
  templateUrl: 'html/host.overview.comp.html'
})
export class HostOverview implements AfterViewInit {

  private _searchQuery: string = null;
  private _searchTemplateQuery: string = null;

  private _hosts: Array<Host> = [];

  private autorefresh: boolean = false;

  private templates: Array<Template> = []

  constructor(private hostHttp: HostHttpService, private router: Router,
              private templateHttp: TemplateHttpService) {
  };

  ngAfterViewInit(): void {
    this.loadData();
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

  protected reloadHosts(): void {
    this.loadData();
  }

  protected gotoDetails(host: Host) {
    this.router.navigate(['host', host.name]);
  }

  protected isAlive(host: Host): boolean {
    let now = new Date();
    return now.getMilliseconds() - host.lastSeen < 30 * 1000 * 60;
  }

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
      return host.template == templateQuery;
    }
    return true;
  }
}
