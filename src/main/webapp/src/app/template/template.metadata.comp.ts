import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { throwError as observableThrowError, Observable, Subscription, forkJoin, of} from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ServiceDefaultState, Template, TemplateHttpService } from '../util/http/template.http.service';
import { SettingHttpService, Settings } from '../util/http/setting.http.service';
import { Repo, RepoHttpService } from '../util/http/repo.http.service';
import { forbiddenNameValidator, Validator } from '../util/validator.util';
import { Sorter } from '../util/sorters.util';
import { AlertService } from '../util/alert/alert.service';
import { Mode } from '../util/enums.util';

interface TemplateForm extends Partial<Template> {
  copyFrom: string;
}

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
export class TemplateMetaData implements OnInit, OnDestroy {

  @Input() obsTemplate: Observable<Template>;
  @Input() mode: Mode = Mode.EDIT;

  public modes = Mode;
  public template: Template = {name: '', description: ''};

  public existingTemplateNames: Observable<string[]>;

  public settings: Settings = {};

  public allRepos: Repo[] = [];
  public showNewRepo = false;

  public templateForm: FormGroup;
  public copyFrom: FormControl;

  public groups: string[] = [];

  private _templateSub: Subscription;
  private _settingsSub: Subscription;

  constructor(private readonly route: ActivatedRoute,
              private readonly settingsHttp: SettingHttpService,
              private readonly templateHttp: TemplateHttpService,
              private readonly repoHttp: RepoHttpService,
              private readonly alerts: AlertService,
              private readonly router: Router,
              private readonly fb: FormBuilder) {
    this.copyFrom = fb.control('');
    this.templateForm = fb.group({
      name: ['', [Validators.required, forbiddenNameValidator('new')]],
      description: [''],
      autoUpdate: false,
      smoothUpdate: false,
      copyFrom: this.copyFrom,
      newRepo: '',
      group: ''
    });
  }

  ngOnInit(): void {
    this._templateSub = this.obsTemplate.subscribe((template) => {
      this.template = template;

      // update metadata form
      this.templateForm.controls.name.setValue(template.name);
      this.templateForm.controls.description.setValue(template.description);
      this.templateForm.controls.autoUpdate.setValue(template.autoUpdate);
      this.templateForm.controls.smoothUpdate.setValue(template.smoothUpdate);
      this.templateForm.controls.group.setValue(template.group);
    });

    this._settingsSub = this.settingsHttp.getSettings().subscribe(
      (result) => this.settings = result,
      (err) => console.error(err)
    );

    this.templateHttp.getSimpleTemplates().subscribe((result) => {
      result.forEach((t) => {
        if (!this.groups.some((e) => e === t.group)) {
          this.groups.push(t.group);
        }
      });
    });

    if (this.mode === Mode.NEW) {
      this.existingTemplateNames = this.templateHttp.getTemplateNames();
    }
  }

  ngOnDestroy(): void {
    if (this._templateSub) {
      this._templateSub.unsubscribe();
    }

    if (this._settingsSub) {
      this._settingsSub.unsubscribe();
    }
  }

  public saveTemplateMetadata(formValue: TemplateForm): void {
    let templateToSave: Template = {
      name: formValue.name.trim(),
      description: formValue.description || '',
      repos: this.template.repos,
      versions: this.template.versions,
      hosts: this.template.hosts,
      autoUpdate: formValue.autoUpdate,
      smoothUpdate: formValue.smoothUpdate,
      group: formValue.group
    };

    if (this.mode === Mode.NEW) {
      let serviceDefaultStates: ServiceDefaultState[] = [];

      this.templateHttp.existsTemplate(templateToSave.name).pipe(
        mergeMap((templateExists) => {
          if (templateExists) {
            return observableThrowError(`Template '${templateToSave.name}' does already exist!`);
          }

          let templateObs: Observable<Partial<Template>> = of({});
          let sdsObs: Observable<ServiceDefaultState[]> = of([]);
          if (Validator.notEmpty(formValue.copyFrom)) {
            templateObs = this.templateHttp.getTemplate(formValue.copyFrom);
            sdsObs = this.templateHttp.getServiceDefaultStates(formValue.copyFrom)
          }

          return forkJoin(templateObs, sdsObs)
        }),
        mergeMap(([templateToCopy = {}, sds = []]) => {
          serviceDefaultStates = sds;

          // overwrite repos and versions with template to be copied
          templateToSave = {...templateToSave, repos: templateToCopy.repos, versions: templateToCopy.versions};
          return this.templateHttp.save(templateToSave)
        }),
        mergeMap(() => {
          return this.updateServiceStates(templateToSave.name, serviceDefaultStates);
        })
      ).subscribe(() => {
          this.alerts.success(`Successfully created template '${templateToSave.name}'.`);
          this.router.navigate(['template', templateToSave.name]);
        }, (err) => {
          this.alerts.danger(`Failed to create template '${templateToSave.name}': ${err}`);
          console.error(err);
        }
      );
    } else {
      this.templateHttp.save(templateToSave).subscribe(() => {
        this.alerts.success(`Successfully saved template '${templateToSave.name}'.`);
        if (this.mode === Mode.EDIT) {
          return;
        }
        this.router.navigate(['template', templateToSave.name]);
      }, (err) => {
        this.alerts.danger(`Failed to save template '${templateToSave.name}'`);
        console.error(err);
      });
    }
  }

  private updateServiceStates(templateName: string, serviceDefaultStates: ServiceDefaultState[]): Observable<ServiceDefaultState[]> {
    const sdsUpdates = serviceDefaultStates.map(sds => this.templateHttp.saveServiceDefaultState(templateName, sds.service, sds.state));
    return forkJoin(sdsUpdates);
  }

  public deleteTemplate(): void {
    this.templateHttp.deleteTemplate(this.template).subscribe(
      () => {
        this.alerts.success(`Successfully deleted template '${this.template.name}'`);
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['template']);
      },
      () => {
        this.alerts.danger(`Failed to delete template '${this.template.name}'`);
      }
    );
  }

  public addNewRepo(newRepo: string): void {
    if (Validator.notEmpty(newRepo)) {
      const newRepos = [...this.template.repos, newRepo];
      newRepos.sort();

      this.template.repos = newRepos;
      this.settings.disallowUninstall.sort();
      this.templateForm.controls.newRepo.setValue('');
      this.showNewRepo = false;

      if (this.mode === Mode.EDIT) {
        this.saveTemplateMetadata(this.templateForm.value)
      }
    }
  }

  public removeRepo(repoName: string): void {
    if (Validator.notEmpty(repoName)) {
      this.template.repos.splice(this.template.repos.indexOf(repoName), 1);
      if (this.mode === Mode.EDIT) {
        this.saveTemplateMetadata(this.templateForm.value)
      }
      if (this.showNewRepo) {
        this.goToAddRepo();
      }
    }
  }

  public goToAddRepo(): void {
    this.templateForm.controls.newRepo.setValue('');
    this.showNewRepo = true;
    this.repoHttp.getRepos().subscribe((repos) => {
        this.allRepos = repos.filter((repo) => !this.template.repos.includes(repo.name)).sort(Sorter.repo);
    });
  }

}
