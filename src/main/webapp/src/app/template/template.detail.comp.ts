import { Component, AfterViewInit } from "@angular/core";
import { TemplateHttpService, Template } from "../util/http/template.http.service";
import { ActivatedRoute } from "@angular/router";
import { Validator } from "../util/validator.util";
import { BehaviorSubject, Observable } from "rxjs";
import { Mode } from "../util/enums.util";

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
export class TemplateDetail implements AfterViewInit {

  private _template: BehaviorSubject<Template> = new BehaviorSubject({name: "", description: ""});
  public template: Observable<Template> = this._template.asObservable();
  private currentTemplate: Template = {name: "", description: "", hosts:[]};
  protected autorefesh: boolean = false;

  protected modes = Mode;

  constructor(private templateHttp: TemplateHttpService,
              private route: ActivatedRoute) {
  };

  ngAfterViewInit(): void {
    this.route.params.subscribe((params) => {
      this.loadTemplate(params['templateName']);
    });
    this.template.subscribe(
      (result) => this.currentTemplate = result
    )
  }

  private loadTemplate(templateName: string) {
    if (Validator.notEmpty(templateName) && templateName != 'new') {
      this.templateHttp.getTemplate(templateName).subscribe(
        (result) => {
          result.repos.sort();
          this._template.next(result);
        }
      )
    }
  }

  protected reloadTemplate(): void {
    if (this.currentTemplate) {
      this.loadTemplate(this.currentTemplate.name);
    }
  }


}
