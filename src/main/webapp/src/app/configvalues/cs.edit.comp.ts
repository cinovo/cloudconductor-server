import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { ConfigValueHttpService, ConfigValue } from '../util/http/configValue.http.service';
import { AlertService } from '../util/alert/alert.service';
import { forbiddenNameValidator, Validator } from '../util/validator.util';
import { ServiceHttpService } from '../util/http/service.http.service';
import { Sorter } from '../util/sorters.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cs-edit',
  templateUrl: './cs.edit.comp.html'
})
export class ConfigValueEdit implements OnInit {

  public kvForm: FormGroup;

  public template: string;
  public mode = 'new';
  public templates: Array<String> = [];
  public services: Array<String> = [];

  constructor(private configHttp: ConfigValueHttpService,
              private route: ActivatedRoute,
              private alerts: AlertService,
              private router: Router,
              private serviceHttp: ServiceHttpService,
              private fb: FormBuilder) {
    this.kvForm = this.fb.group({
      key: ['', [Validators.required, forbiddenNameValidator('new')]],
      value: ['', Validators.required],
      template: '',
      service: ''
    });
  };

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      let template = params['template'] || '';
      const service = params['service'] || '';
      const key = params['key'] || '';
      const value = params['value'] || '';

      this.template = template;

      if (template === 'newTemplate') {
        template = '';
      }
      let formObj = {key, value, template, service};
      this.kvForm.setValue(formObj);

      if (Validator.notEmpty(key)) {
        this.mode = 'edit';
        this.configHttp.getConfigValue(template, service, key).subscribe(
          (result) => {
            formObj.value = result;
            this.kvForm.setValue(formObj);
          },
          (err) => console.error(err));
      }
    });
    this.configHttp.templates.subscribe((result) => this.templates = result);
    this.serviceHttp.getServices().subscribe((result) => {
        this.services = result.sort(Sorter.service).map((val) => val.name);
      });
  }

  public save(newConfigPair: ConfigValue): void {
    let check: Observable<boolean>;
    if (this.mode === 'new') {
      check = this.configHttp.existsConfigValue(newConfigPair.template, newConfigPair.service, newConfigPair.key);
    } else {
      check = Observable.of(false);
    }

    check.flatMap(exists => {
      if (exists) {
        return Observable.throw(`Configuration for '${newConfigPair.key}' does already exist!`);
      } else {
        return this.configHttp.save(newConfigPair);
      }
    }).subscribe(
      () => {
        let verb = (this.mode === 'new') ? 'created' : 'updated';
        this.alerts.success(`Successfully ${verb} key-value pair: '${newConfigPair.key} - ${newConfigPair.value}'.`);
        this.router.navigate(['/config', newConfigPair.template])
      }, (err) => {
        this.alerts.danger(`Error creating new key-value pair '${newConfigPair.key} - ${newConfigPair.value}': ${err}`);
        console.error(err);
      }
    );
  }

}
