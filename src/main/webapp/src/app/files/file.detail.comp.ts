import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router/';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { ConfigFile, FileForm, FileType  } from '../util/http/config-file.model';
import { FileHttpService } from '../util/http/file.http.service';
import { forbiddenNameValidator } from '../util/validator.util';
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
export class FileDetailComponent implements OnInit, OnDestroy {

  public file = new ConfigFile();

  public serviceNames: Observable<string[]>;
  public templateNames: Observable<string[]>;

  public fileForm: FormGroup;

  public formVerb = 'Edit';
  public formObj = 'File';

  private _createMode = false;

  private _dataSub: Subscription;

  constructor(private alertService: AlertService,
              private fileHttpService: FileHttpService,
              private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private serviceHttpService: ServiceHttpService,
              private templateHttpService: TemplateHttpService) {

    this.fileForm = this.formBuilder.group({
      name: [this.file.name, [Validators.required, forbiddenNameValidator('new')]],
      owner: [this.file.owner, Validators.required],
      group: [this.file.group, Validators.required],
      fileMode: [this.file.fileMode, [Validators.required, Validators.pattern(/^[0-7]{3}$/)]],
      isTemplate: this.file.isTemplate,
      isReloadable: this.file.isReloadable,
      targetPath: [this.file.targetPath, [Validators.required, Validators.pattern(/^(\/[^/ ]+)+\/?$/)]],
      dependentServices: [this.file.dependentServices],
      templates: [this.file.templates],
      fileContent: '',
      type: 'File'
    });
  }

  ngOnInit(): void {
    this.serviceNames = this.serviceHttpService.getServiceNames();
    this.templateNames = this.templateHttpService.getTemplateNames();

    this._dataSub = this.route.data.subscribe((data) => {
      if (data.fileForm) {
        this.fileForm.setValue(data.fileForm);
        this.createMode = false;
        this.formObj = data.fileForm.type;
      } else {
        this.createMode = true;
      }
    });
  }

  ngOnDestroy(): void {
    if (this._dataSub) {
      this._dataSub.unsubscribe();
    }
  }

  get createMode() {
    return this._createMode;
  }

  set createMode(value: boolean) {
    this._createMode = value;
    this.formVerb = (this._createMode) ? 'Create' : 'Edit';
  }

  get formTitle() {
    return `${this.formVerb} ${this.fileForm.value.type}`;
  }

  public saveFile(fv: any): void {
    const fileForm = Object.assign(new FileForm(), fv);
    const updatedFile: ConfigFile = fileForm.toConfigFile();

    let check: Observable<boolean>;
    if (this._createMode) {
      check = this.fileHttpService.existsFile(updatedFile.name);
    } else {
      check = Observable.of(false);
    }

    check.flatMap(exists => {
      if (exists) {
        return Observable.throw(`File or directory named '${updatedFile.name}' already exists!`);
      } else {
        console.log(`Update file '${updatedFile.name}'`);
        return this.fileHttpService.updateFile(updatedFile);
      }
    }).flatMap(() => {
      if (updatedFile.isDirectory) {
        // updating content is useless here
        return Observable.of(true);
      } else {
        console.log('Update file content...');
        return this.fileHttpService.updateFileData(updatedFile.name, fv.fileContent);
      }
    }).subscribe(
      () => {
        const verb = (this._createMode) ? 'created' : 'updated' ;

        this.alertService.success(`Successfully ${verb} ${this.formObj.toLowerCase()} '${updatedFile.name}'!`);
        this.router.navigate(['/files']);
      },
      (err) => {
        this.alertService.danger(`Error saving ${this.formObj.toLowerCase()} '${updatedFile.name}': ${err}`);
        console.error(err);
      }
    );
  }

}
