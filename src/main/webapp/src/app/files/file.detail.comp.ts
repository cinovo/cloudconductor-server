import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router/';

import { Observable } from 'rxjs/Observable';

import { AlertService } from '../util/alert/alert.service';
import { ConfigFile, FileForm, FileType  } from '../util/http/config-file.model';
import { FileHttpService } from '../util/http/file.http.service';
import { ServiceHttpService } from '../util/http/service.http.service';
import { TemplateHttpService } from '../util/http/template.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './file.detail.comp.html'
})
export class FileDetailComponent implements OnInit {

  public file = new ConfigFile();

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
    const updatedFile: ConfigFile = fv.toConfigFile();

    this.fileHttpService.updateFile(updatedFile).flatMap(() => {
      if (updatedFile.isDirectory) {
        // updating content is useless here
        return Observable.of(true);
      } else {
        console.log('Update file content...');
        return this.fileHttpService.updateFileData(updatedFile.name, fv.fileContent);
      }
    }).subscribe(() => {
      this.alertService.success(`Successfully saved ${this.formObj} '${updatedFile.name}'!`);
      this.router.navigate(['/files']);
    },
    (err) => {
      this.alertService.danger(`Error saving ${this.formObj} '${updatedFile.name}'!`);
      console.error(err);
    });
  }

}
