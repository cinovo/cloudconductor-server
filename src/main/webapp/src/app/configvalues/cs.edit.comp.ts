import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ConfigValueHttpService, ConfigValue } from '../util/http/configValue.http.service';
import { AlertService } from '../util/alert/alert.service';
import { Validator } from '../util/validator.util';
import { ServiceHttpService } from '../util/http/service.http.service';

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
      key: ['', Validators.required],
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
      const formObj = {key, value, template, service};
      this.kvForm.setValue(formObj);

      if (Validator.notEmpty(key)) {
        this.mode = 'edit';
        this.configHttp.getConfigValue(template, service, key)
          .subscribe((result) => {
            formObj.value = result;
            this.kvForm.setValue(formObj);
          }, (err) => console.error(err));
      }
    });
    this.configHttp.templates.subscribe((result) => this.templates = result);
    this.serviceHttp.getServices().subscribe((result) => {
        this.services = result.map((val) => {return val.name});
      });
  }

  public save(newConfigPair): void {
    this.configHttp.save(newConfigPair).subscribe(() => {
        this.alerts.success('Successfully created new key-value pair!');
        this.router.navigate(['config', newConfigPair.template])
      }, (err) => {
        this.alerts.danger('Error creating new key-value pair!');
        console.error(err);
      }
    );
  }

}
