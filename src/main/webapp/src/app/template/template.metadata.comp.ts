import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { Template, TemplateHttpService } from '../util/http/template.http.service';
import { Settings, SettingHttpService } from '../util/http/setting.http.service';
import { Repo, RepoHttpService } from '../util/http/repo.http.service';
import { forbiddenNameValidator, Validator } from '../util/validator.util';
import { Sorter } from '../util/sorters.util';
import { AlertService } from '../util/alert/alert.service';
import { Mode } from '../util/enums.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'template-metadata',
  templateUrl: './template.metadata.comp.html'
})
export class TemplateMetaData implements OnInit {

  @Input() obsTemplate: Observable<Template>;
  @Input() mode: Mode = Mode.EDIT;

  public modes = Mode;
  public template: Template = {name: '', description: ''};

  public existingTemplateNames: Array<string> = [];

  public settings: Settings = {};

  protected allRepos: Array<Repo> = [];
  public newRepo = '';
  public showNewRepo = false;

  public templateForm: FormGroup;
  public copyFrom: FormControl;

  constructor(private route: ActivatedRoute,
              private settingsHttp: SettingHttpService,
              private templateHttp: TemplateHttpService,
              private repoHttp: RepoHttpService,
              private alerts: AlertService,
              private router: Router,
              private fb: FormBuilder) {
    this.copyFrom = fb.control('');
    this.templateForm = fb.group({
      name: ['', [Validators.required, forbiddenNameValidator('new')]],
      description: [''],
      autoUpdate: false,
      smoothUpdate: false,
      copyFrom: this.copyFrom,
      newRepo: ''
    });
  }

  ngOnInit(): void {
    this.obsTemplate.subscribe((result) => {
      this.template = result;

      // update metadata form
      this.templateForm.controls.name.setValue(result.name);
      this.templateForm.controls.description.setValue(result.description);
      this.templateForm.controls.autoUpdate.setValue(result.autoUpdate);
      this.templateForm.controls.smoothUpdate.setValue(result.smoothUpdate);
    });

    this.settingsHttp.getSettings().subscribe(
      (result) => this.settings = result
    );

    if (this.mode === Mode.NEW) {
      this.templateHttp.getTemplates().subscribe((result) => {
        for (let t of result) {
          this.existingTemplateNames.push(t.name);
        }
        this.existingTemplateNames.sort();
      });
    }
  }

  public saveTemplateMetadata(formValue): void {
    console.log({formValue});

    const templateToSave: Template = {
      name: formValue.name,
      description: formValue.description || '',
      repos: this.template.repos,
      versions: this.template.versions,
      hosts: this.template.hosts,
      autoUpdate: formValue.autoUpdate,
      smoothUpdate: formValue.smoothUpdate
    }

    if (this.mode === Mode.NEW) {
      let templateObs: Observable<any>;

      if (Validator.notEmpty(formValue.copyFrom)) {
        templateObs = this.templateHttp.getTemplate(formValue.copyFrom);
      } else {
        templateObs = Observable.of({});
      }

      templateObs.flatMap((result) => {
          // first overwrite repos and versions with template to be copied
          if (result.repos && result.repos.length > 0) {
            templateToSave.repos = result.repos;
          }
          if (result.versions) {
            templateToSave.versions = result.versions;
          }

          // then check whether name is already in use
          return this.templateHttp.existsTemplate(templateToSave.name);
        }).flatMap((exists) => {
          if (!exists) {
            // alright, try to save template...
            return this.templateHttp.save(templateToSave);
          } else {
            return Observable.throw(`template named ${templateToSave.name} does already exist!`);
          }
        }).subscribe(() => {
          this.alerts.success('Successfully saved the template ' + templateToSave.name);
          this.router.navigate(['template', templateToSave]);
        }, (err) => {
          this.alerts.danger('Failed to save template!');
          console.error(err);
        }
      );
    } else {
      this.templateHttp.save(templateToSave).subscribe(() => {
          this.alerts.success('Successfully saved the template ' + templateToSave.name);
          if (this.mode === Mode.EDIT) {
            return;
          }
          this.router.navigate(['template', templateToSave.name]);
        }, (err) =>  {
          this.alerts.danger('Failed to save template ' + formValue.copyFrom);
          console.error(err);
        });
    }
  }

  protected deleteTemplate(): void {
    this.templateHttp.deleteTemplate(this.template).subscribe(
      () => {
        this.alerts.success(`Successfully deleted template '${this.template.name}'`);
        this.router.navigate(['template']);
      },
      () => {
        this.alerts.danger(`Failed to delete template '${this.template.name}'`);
      }
    );
  }

  protected addNewRepo(newRepo: string): void {
    if (newRepo) {
      this.template.repos.push(this.newRepo);
      this.template.repos.sort();
      this.settings.disallowUninstall.sort();
      this.newRepo = null;
      this.showNewRepo = false;
    }
  }

  protected removeRepo(repoName: string): void {
    if (Validator.notEmpty(repoName)) {
      this.template.repos.splice(this.template.repos.indexOf(repoName), 1);
      if (this.showNewRepo) {
        this.goToAddRepo();
      }
    }
  }

  protected goToAddRepo(): void {
    this.newRepo = '';
    this.showNewRepo = true;
    this.repoHttp.getRepos().subscribe(
      (result) => {
        this.allRepos = result.filter(
          (repo) => {
            return !this.template.repos.includes(repo.name)
          }
        ).sort(Sorter.repo);
        if (this.allRepos.length > 0) {
          this.newRepo = this.allRepos[0].name;
        }
      }
    );
  }

}
