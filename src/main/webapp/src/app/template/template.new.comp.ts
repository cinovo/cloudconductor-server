import { Component, OnInit } from '@angular/core';

import { BehaviorSubject ,  Observable } from 'rxjs';

import { Template } from '../util/http/template.http.service';
import { Mode } from '../util/enums.util';

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
export class TemplateNew implements OnInit {

  private _template: BehaviorSubject<Template> = new BehaviorSubject({name: '', description: ''});
  public template: Observable<Template> = this._template.asObservable();

  modes = Mode;

  constructor() { };

  ngOnInit(): void {
    this._template.next({name: '', description: '', repos: [], versions: {}, autoUpdate: false, smoothUpdate: false});
  }

}
