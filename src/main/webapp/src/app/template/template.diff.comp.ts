import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AlertService } from '../util/alert/alert.service';
import { PackageDiff, SimpleTemplate, TemplateHttpService } from '../util/http/template.http.service';
import { Sorter } from "../util/sorters.util";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'template-diff',
  templateUrl: './template.diff.comp.html'
})
export class TemplateDiff implements OnInit {

  public templates: Array<SimpleTemplate> = [];

  private _templateA: string;
  private _templateB: string;
  public diffResult: PackageDiff[];
  public templatesLoaded: boolean = false;
  public loaded: boolean = false;

  constructor(private templateHttp: TemplateHttpService,
              private router: Router,
              private alerts: AlertService) {
  };

  ngOnInit(): void {
    this.loadTemplates();
  }


  private loadTemplates(): void {
    this.templateHttp.getTemplates().subscribe((result) => {
      this.templates = result;
      if (this.templates && this.templates.length > 2) {
        this._templateA = this.templates[0].name;
        this._templateB = this.templates[1].name;
        this.diffTemplates();
      }
      this.templatesLoaded = true;
    }, (err) => {
      this.alerts.danger('Error loading templates!');
    });
  }

  private diffTemplates(): void {
    this.diffResult = null;
    this.loaded = false;
    if (this.templateA && this.templateB) {
      this.templateHttp.getDiff(this.templateA, this.templateB).subscribe((result) => {
        this.diffResult = result;
        this.diffResult.sort(Sorter.nameField);
        this.loaded = true;
      }, (err) => {
        this.alerts.danger('Error loading difference between the two packages!');
      });
    }
  }


  get templateA(): string {
    return this._templateA;
  }

  set templateA(value: string) {
    this._templateA = value;
    this.diffTemplates();
  }

  get templateB(): string {
    return this._templateB;
  }

  set templateB(value: string) {
    this._templateB = value;
    this.diffTemplates();
  }
}
