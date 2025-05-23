import { Component, OnInit } from "@angular/core";
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";

import { throwError as _throw } from "rxjs";
import { finalize, switchMap } from 'rxjs/operators';

import { ConfigValue, ConfigValueHttpService } from "../util/http/configValue.http.service";
import { ServiceHttpService } from "../util/http/service.http.service";
import { AlertService } from "../util/alert/alert.service";
import { Validator } from "../util/validator.util";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
    selector: 'cs-new',
    templateUrl: './cs.new.comp.html',
    standalone: false
})
export class ConfigValueNew implements OnInit {

  public kvForm: UntypedFormGroup;
  public templates: string[] = [];
  public working = false;

  constructor(private readonly configHttp: ConfigValueHttpService,
              private readonly route: ActivatedRoute,
              private readonly alerts: AlertService,
              private readonly router: Router,
              private readonly serviceHttp: ServiceHttpService,
              private readonly fb: UntypedFormBuilder) {
    this.kvForm = this.fb.group({
      template: [''],
      newTemplate: ['', [Validators.required, Validators.pattern(/^[\w.-]+$/)]],
      prefil: ['none', [Validators.required]],
      importValues: ['']
    }, {validator: ConfigValueNew.fullValidator});
  }

  ngOnInit(): void {
    this.configHttp.templates.subscribe((result) => this.templates = result);
  }

  public save() {
    this.working = true;

    const cvToSave = {key: "firstKey", value: "empty", template: this.kvForm.value.newTemplate.trim()};
    if (this.kvForm.value.prefil === 'none') {
      this.configHttp.save(cvToSave).subscribe((result) => {
          this.working = false;
          this.alerts.success(`Successfully created new template '${cvToSave.template}' .`);
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['/config', cvToSave.template]);
        }
      );
      return;
    }
    if (this.kvForm.value.prefil === 'copy') {
      this.copyFromTemplate();
    }
    if (this.kvForm.value.prefil === 'importjson') {
      this.importFromJSON();
    }
    if (this.kvForm.value.prefil === 'import') {
      this.importFromJSON();
    }
  }

  private copyFromTemplate(): void {
    const existingTemplate = this.kvForm.value.template;
    const newTemplate = this.kvForm.value.newTemplate;

    this.configHttp.getValues(existingTemplate).pipe(
      switchMap((originConfigs: ConfigValue[]) => {
        if (!originConfigs) {
          return _throw(new Error("No configs to copy"));
        }

        const newConfigs = originConfigs.map((cv: ConfigValue) => ({...cv, template: newTemplate}));
        return this.configHttp.saveBulk(newConfigs)
      }),
      finalize(() => this.working = false),
    ).subscribe(() => {
        this.alerts.success(`Successfully created new template '${newTemplate}' and copied values from '${existingTemplate}'.`);
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['/config', newTemplate]);
      }, (err) => {
        this.alerts.danger(`Error creating new template '${newTemplate}'!`);
        console.error(err);
      });
  }

  private importFromJSON(): void {
    if (!Validator.notEmpty(this.kvForm.value.importValues)) {
      this.working = false;
      return;
    }

    const newTemplate = this.kvForm.value.newTemplate;
    const importedValues: ConfigValue[] = [];
    try {
      const json: any[] = JSON.parse(this.kvForm.value.importValues);
      for (let val of json) {
        if ('key' in val && 'value' in val) {
          const newElement: ConfigValue = {key: val.key, value: val.value, template: newTemplate, service: val.service};
          importedValues.push(newElement);
        }
      }
    } catch (e) {
      this.working = false;
      this.alerts.danger("Failed to read the given json. No proper json!");
      return;
    }

    if (importedValues.length < 1) {
      this.alerts.danger("Failed to import the given json, no config values found!");
      this.working = false;
      return;
    }

    this.configHttp.saveBulk(importedValues).pipe(
      finalize(() => this.working = false))
      .subscribe(() => {
        this.alerts.success(`Successfully created new template '${newTemplate}' and imported ${importedValues.length} values.`);
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['/config', newTemplate]);
      }, (err) => {
        this.alerts.danger(`Error creating new template '${newTemplate}'!`);
        console.error(err);
      });
  }

  updateFile(event: any): void {
    const reader = new FileReader();
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      reader.readAsText(file);
      reader.onloadend = () => {
        this.kvForm.patchValue({importValues: reader.result}, {emitEvent: true});
      };
    }
  }

  static fullValidator(control: AbstractControl): ValidationErrors | null {
    const prefil = control.get('prefil');
    if (prefil.value === 'none') {
      return null;
    }
    if (prefil.value === 'copy') {
      const template = control.get('template');
      return Validators.required(template);
    }
    if (prefil.value === 'import') {
      const importValues = control.get('importValues');
      return Validators.required(importValues);
    }
  }

}
