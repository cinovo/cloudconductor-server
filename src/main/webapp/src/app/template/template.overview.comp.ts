import { Component, AfterViewInit } from "@angular/core";
import { TemplateHttpService, Template } from "../util/http/template.http.service";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";
import { Router } from "@angular/router";
import { AlertService } from "../util/alert/alert.service";
/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  moduleId: module.id,
  selector: 'template-overview',
  templateUrl: 'html/template.overview.comp.html'
})
export class TemplateOverview implements AfterViewInit {

  private _searchQuery: string = null;

  private _templates: Array<Template> = [];

  constructor(private templateHttp: TemplateHttpService, private router: Router, private alerts: AlertService) {
  };

  ngAfterViewInit(): void {
    this.loadTemplates();
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
        this.alerts.success("The template " + template.name + " has been deleted.")
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


  private static filterData(template: Template, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return template.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }
}
