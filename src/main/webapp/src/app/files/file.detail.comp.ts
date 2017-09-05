import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router/';

import { Observable } from 'rxjs/Observable';

import { AlertService } from '../util/alert/alert.service';
import { ConfigFile, FileHttpService } from '../util/http/file.http.service';
import { ServiceHttpService } from '../util/http/service.http.service';
import { TemplateHttpService } from '../util/http/template.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
export interface FileForm {
  name: string,
  owner: string,
  group: string,
  fileMode: string,
  isTemplate: boolean,
  targetPath: string,
  dependentServices: string[],
  servicesReload: string[],
  templates: string[],
  fileContent: string,
  type: FileType
}

export enum FileType {
  File = 'File',
  Directory = 'Directory'
}

@Component({
  templateUrl: './file.detail.comp.html'
})
export class FileDetailComponent implements OnInit {

  public file: ConfigFile = {
    name: '', pkg: '', targetPath: '', owner: '', group: '', fileMode: '644',
    isTemplate: false, isReloadable: false, isDirectory: false, dependentServices: [], templates: []
  };

  public serviceNames: Observable<string[]>;
  public templateNames: Observable<string[]>;

  public fileForm: FormGroup;

  public formVerb = 'Edit';
  public formObj = 'File';

  public _createMode = false;

  constructor(private alertService: AlertService,
              private fileHttpService: FileHttpService,
              private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private serviceHttpService: ServiceHttpService,
              private templateHttpService: TemplateHttpService) {

    this.fileForm = this.formBuilder.group({
      name: [this.file.name, Validators.required],
      owner: [this.file.owner, Validators.required],
      group: [this.file.group, Validators.required],
      fileMode: [this.file.fileMode, Validators.required],
      isTemplate: this.file.isTemplate,
      targetPath: [this.file.targetPath, Validators.required],
      dependentServices: [this.file.dependentServices],
      servicesReload: [['']],
      templates: [this.file.templates],
      fileContent: '',
      type: 'File'
    });
  }

  ngOnInit(): void {
    this.serviceNames = this.serviceHttpService.getServiceNames();
    this.templateNames = this.templateHttpService.getTemplateNames();
    this.route.data.subscribe((data) => {
      if (data.fileForm) {
        this.fileForm.setValue(data.fileForm);
        this.createMode = false;
        this.formObj = data.fileForm.type;
      } else {
        this.createMode = true;
      }
    });
  }

  get createMode() {
    return this._createMode;
  }

  set createMode(value: boolean) {
    this._createMode = value;
    this.formVerb = (this._createMode) ? 'Create' : 'Edit';
  }

  get formTitle() {
    return `${this.formVerb} ${this.formObj}`;
  }

  public toggleFileMode() {
    this.formObj = this.fileForm.value.type;
  }

  public saveFile(fv: FileForm): void {

    console.log({fv});

    const updatedFile: ConfigFile = {
      name: fv.name,
      pkg: '',
      targetPath: fv.targetPath,
      owner: fv.owner,
      group: fv.group,
      fileMode: fv.fileMode,
      isDirectory: (fv.type === FileType.Directory),
      isTemplate: false,
      isReloadable: fv.servicesReload.length > 0,
      dependentServices: fv.dependentServices,
      templates: fv.templates
    }

    console.log({updatedFile});

    this.fileHttpService.updateFile(updatedFile).flatMap(() => {
      console.log('Update file content...');
      return this.fileHttpService.updateFileData(updatedFile.name, fv.fileContent);
    }).subscribe(() => {
      this.alertService.success(`Successfully saved file '${updatedFile.name}'!`);
      this.router.navigate(['/files']);
    },
    (err) => {
      this.alertService.danger(`Error saving file '${updatedFile.name}'!`);
      console.error(err);
    });
  }

}
