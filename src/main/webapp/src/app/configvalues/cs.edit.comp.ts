import { Component, AfterViewInit } from "@angular/core";
import { ConfigValueHttpService, ConfigValue } from "../util/http/configValue.http.service";
import { ActivatedRoute, Router } from "@angular/router";
import { AlertService } from "../util/alert/alert.service";
import { Validator } from "../util/validator.util";
import { ServiceHttpService } from "../util/http/service.http.service";
/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  moduleId: module.id,
  selector: 'cs-edit',
  templateUrl: 'html/cs.edit.comp.html'
})
export class ConfigValueEdit implements AfterViewInit {

  private cv: ConfigValue = {template: "", service: "", key: "", value: ""};
  private mode: string = 'new';
  protected templates: Array<String> = [];
  protected services: Array<String> = [];

  constructor(private configHttp: ConfigValueHttpService, private route: ActivatedRoute,
              private alerts: AlertService, private router: Router,
              private serviceHttp: ServiceHttpService) {
  };

  ngAfterViewInit(): void {
    this.route.params.subscribe((params) => {
      this.cv = {template: params['template'], service: params['service'], key: params['key'], value: ""};
      if (this.cv.template == 'newTemplate') {
        this.cv.template = '';
      }
      if (Validator.notEmpty(this.cv.key)) {
        this.mode = 'edit';
        this.configHttp.getConfigValue(this.cv.template, this.cv.service, this.cv.key)
          .subscribe(
            (result) =>
              this.cv.value = result,
            () => {
            }
          );
      }
    });
    this.configHttp.templates.subscribe((result) => this.templates = result);
    this.serviceHttp.getServices().subscribe(
      (result) => {
        this.services = result.map((val) => {return val.name});
      });
  }

  protected save(): void {
    if (this.fieldValidation()) {
      return;
    }
    this.configHttp.save(this.cv).subscribe(
      () => {
        this.router.navigate(["config", this.cv.template])
      }
    );
  }


  private fieldValidation() {
    let error = false;
    if (!this.isKeyValid()) {
      this.alerts.danger("Please insert a key.");
      error = true;
    }
    if (!this.isValueValid()) {
      this.alerts.danger("Please insert a value.");
      error = true;
    }
    if (!this.isTemplateValid()) {
      this.alerts.danger("Please insert a template.");
      error = true;
    }
    return error;
  }

  private isKeyValid(): boolean {
    return Validator.notEmpty(this.cv.key);
  }

  private isValueValid(): boolean {
    return Validator.notEmpty(this.cv.value);
  }

  private isTemplateValid(): boolean {
    return Validator.notEmpty(this.cv.template);
  }

}
