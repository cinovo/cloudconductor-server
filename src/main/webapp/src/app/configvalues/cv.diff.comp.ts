import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AlertService } from '../util/alert/alert.service';
import { Sorter } from "../util/sorters.util";
import { ConfigDiff, ConfigValueHttpService } from "../util/http/configValue.http.service";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface ConfigDiffGroup {
  group?: string;
  diffs: ConfigDiff[];
}

@Component({
  selector: 'cv-diff',
  templateUrl: './cv.diff.comp.html'
})
export class ConfigValueDiff implements OnInit {
  protected reservedValues: string[] = ['GLOBAL', 'VARIABLES'];

  public templates: string[];

  private _templateA: string;
  private _templateB: string;
  public diffResult: ConfigDiff[];
  public diffTree: ConfigDiffGroup[] = [];

  public templatesLoaded: boolean = false;
  public loaded: boolean = false;

  constructor(private readonly confHttp: ConfigValueHttpService,
              private readonly router: Router,
              private readonly alerts: AlertService) {
  };

  ngOnInit(): void {
    this.loadTemplates();
  }

  private loadTemplates(): void {
    this.confHttp.templates.subscribe((e) => {
      this.templates = e.filter(i => !this.reservedValues.includes(i));
      if (this.templates && this.templates.length > 2) {
        this._templateA = this.templates[0];
        this._templateB = this.templates[1];
        this.diffTemplates();
      }
      this.templatesLoaded = true;
    }, (err) => {
      console.error(err);
      this.alerts.danger('Error loading templates!');
    });
  }

  private diffTemplates(): void {
    this.diffResult = null;
    this.diffTree = [];
    this.loaded = false;
    if (this.templateA && this.templateB) {
      this.confHttp.getDiff(this.templateA, this.templateB).subscribe((result) => {
        this.diffResult = result.filter(i => !this.reservedValues.includes(i.service));
        this.createTree();
        this.loaded = true;
      }, (err) => {
        console.error(err);
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

  private createTree() {
    this.diffResult.forEach((element) => {
      let te = this.diffTree.find((treeElement) => treeElement.group == element.service);
      if (!te) {
        te = {group: element.service, diffs: []};
        this.diffTree.push(te);
      }
      te.diffs.push(element);
    });
    this.diffTree.sort(Sorter.groupField);
  }
}
