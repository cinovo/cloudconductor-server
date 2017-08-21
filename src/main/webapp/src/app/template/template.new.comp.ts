import { Component, AfterViewInit } from "@angular/core";
import { Template } from "../util/http/template.http.service";
import { BehaviorSubject, Observable } from "rxjs";
import { Mode } from "../util/enums.util";
/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'template-new',
  templateUrl: './template.new.comp.html'
})
export class TemplateNew implements AfterViewInit {

  private _template: BehaviorSubject<Template> = new BehaviorSubject({name: "", description: ""});
  public template: Observable<Template> = this._template.asObservable();

  modes = Mode;

  constructor() {
  };

  ngAfterViewInit(): void {
    this._template.next({name: "", description: "", repos:[], versions:{}, autoUpdate:false, smoothUpdate:false});
  }

}
