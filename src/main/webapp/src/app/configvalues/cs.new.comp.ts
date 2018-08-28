import { Component, OnInit } from "@angular/core";
import { ConfigValue, ConfigValueHttpService } from "../util/http/configValue.http.service";
import { ServiceHttpService } from "../util/http/service.http.service";
import { AlertService } from "../util/alert/alert.service";
import { ActivatedRoute, Router } from "@angular/router";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { AbstractControl } from "@angular/forms/src/model";
import { ValidationErrors } from "@angular/forms/src/directives/validators";
import { Validator } from "../util/validator.util";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cs-new',
  templateUrl: './cs.new.comp.html'
})
export class ConfigValueNew implements OnInit {

  public kvForm: FormGroup;
  public templates: Array<String> = [];
  public working = false;

  constructor(private configHttp: ConfigValueHttpService,
              private route: ActivatedRoute,
              private alerts: AlertService,
              private router: Router,
              private serviceHttp: ServiceHttpService,
              private fb: FormBuilder) {
    this.kvForm = this.fb.group({
      template: [''],
      newTemplate: ['', [Validators.required]],
      prefil: ['none', [Validators.required]],
      importValues: ['']
    }, {validator: ConfigValueNew.fullValidator});
  }

  ngOnInit(): void {
    this.configHttp.templates.subscribe((result) => this.templates = result);
  }

  public test() {
    console.log(this.kvForm.value.importValues)
  }

  public save() {
    this.working = true;
    if (this.kvForm.value.prefil == 'none') {
      this.configHttp.save({
        key: "firstKey",
        value: "empty",
        template: this.kvForm.value.newTemplate
      }).subscribe((result) => {
          this.working = false;
          this.alerts.success(`Successfully created new template '${this.kvForm.value.newTemplate}' .`);
          this.router.navigate(['/config', this.kvForm.value.newTemplate]);
        }
      );
      return;
    }
    if (this.kvForm.value.prefil == 'copy') {
      this.copyFromTemplate();
    }
    if (this.kvForm.value.prefil == 'importjson') {
      this.importFromJSON();
    }
    if (this.kvForm.value.prefil == 'import') {
      this.importFromJSON();
    }
  }

  private copyFromTemplate() {
    this.configHttp.getValues(this.kvForm.value.template).subscribe((result) => {
      if (result.length < 1) {
        this.alerts.danger(`Error creating new template '${this.kvForm.value.newTemplate}'!`);
        this.working = false;
        return;
      }
      for (let i = 0; i < result.length; i++) {
        result[i].template = this.kvForm.value.newTemplate;
        if (i == result.length - 1) {
          this.configHttp.save(result[i]).subscribe((result) => {
              this.working = false;
              this.alerts.success(`Successfully created new template '${this.kvForm.value.newTemplate}' and copied values from '${this.kvForm.value.template}'.`);
              this.router.navigate(['/config', this.kvForm.value.newTemplate]);
            }
          );
        } else {
          this.configHttp.save(result[i]);
        }
      }
    }, (err) => {
      this.working = false;
      this.alerts.danger(`Error creating new template '${this.kvForm.value.newTemplate}'!`);
    });
  }

  private importFromJSON() {
    if (!Validator.notEmpty(this.kvForm.value.importValues)) {
      this.working = false;
      return;
    }
    try {
      const json: any[] = JSON.parse(this.kvForm.value.importValues);
      let values: ConfigValue[] = [];
      for (let val of json) {
        if ('key' in val && 'value' in val) {
          let newElement: ConfigValue = {key: val.key, value: val.value, template: this.kvForm.value.newTemplate, service: val.service};
          values.push(newElement);
        }
      }

      if (values.length < 1) {
        this.alerts.danger("Failed to import the given json, there where no config values!");
        this.working = false;
        return;
      }
      for (let i = 0; i < values.length; i++) {
        values[i].template = this.kvForm.value.newTemplate;
        if (i == values.length - 1) {
          this.configHttp.save(values[i]).subscribe((result) => {
              this.working = false;
              this.alerts.success(`Successfully created new template '${this.kvForm.value.newTemplate}'.`);
              this.router.navigate(['/config', this.kvForm.value.newTemplate]);
            }
          );
        } else {
          this.configHttp.save(values[i]);
        }
      }
    } catch (e) {
      this.working = false;
      this.alerts.danger("Failed to read the given json. No proper json!");
    }


  }

  updateFile(event: any): void {
    let reader = new FileReader();
    if (event.target.files && event.target.files.length > 0) {
      let file = event.target.files[0];
      reader.readAsText(file);
      reader.onloadend = () => {
        this.kvForm.patchValue({importValues: reader.result}, {emitEvent: true});
      };
    }
  }

  static fullValidator(control: AbstractControl): ValidationErrors | null {
    const prefil = control.get('prefil');
    if (prefil.value == 'none') {
      return null;
    }
    if (prefil.value == 'copy') {
      const template = control.get('template');
      return Validators.required(template);
    }
    if (prefil.value == 'import') {
      const importValues = control.get('importValues');
      return Validators.required(importValues);
    }
  }

}
