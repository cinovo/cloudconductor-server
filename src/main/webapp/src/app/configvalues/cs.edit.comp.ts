
import { throwError as observableThrowError,  Observable } from 'rxjs';

import { mergeMap } from 'rxjs/operators';
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ConfigValue, ConfigValueHttpService } from '../util/http/configValue.http.service';
import { AlertService } from '../util/alert/alert.service';
import { forbiddenNameValidator } from '../util/validator.util';
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
    templateUrl: './cs.edit.comp.html',
    standalone: false
})
export class ConfigValueEdit implements OnInit {

  public kvForm: UntypedFormGroup;
  public template: string;
  public templates: string[] = [];
  public services: string[] = [];

  constructor(private readonly configHttp: ConfigValueHttpService,
              private readonly route: ActivatedRoute,
              private readonly alerts: AlertService,
              private readonly router: Router,
              private readonly serviceHttp: ServiceHttpService,
              private readonly fb: UntypedFormBuilder) {
    this.kvForm = this.fb.group({
      key: ['', [Validators.required, Validators.pattern('^[\\w.-]+$'), forbiddenNameValidator('new')]],
      value: ['', Validators.required],
      template: '',
      service: ''
    });
  };

  ngOnInit(): void {
    this.route.params.subscribe(({template = '', service = '', key = '', value = ''}) => {
      this.template = template;

      if (template === 'newTemplate') {
        template = '';
      }
      const formObj = {key, value, template, service};
      this.kvForm.setValue(formObj);
    });
    this.configHttp.templates.subscribe((result) => this.templates = result);
    this.serviceHttp.getServices().subscribe((result) => {
      this.services = result.sort(Sorter.service).map((val) => val.name);
    });
  }

  public save(newConfigPair: ConfigValue): void {
    const trimmedCV: ConfigValue = { key: newConfigPair.key.trim(), ...newConfigPair};
    const check: Observable<boolean> = this.configHttp.existsConfigValue(trimmedCV.template, trimmedCV.service, trimmedCV.key);
    check.pipe(mergeMap(exists => {
      if (exists) {
        return observableThrowError(`Configuration for '${trimmedCV.key}' does already exist!`);
      } else {
        return this.configHttp.save(trimmedCV);
      }
    })).subscribe(
      () => {
        this.alerts.success(`Successfully created key-value pair: '${trimmedCV.key} - ${trimmedCV.value}'.`);
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['/config', trimmedCV.template])
      }, (err) => {
        this.alerts.danger(`Error creating new key-value pair '${trimmedCV.key} - ${trimmedCV.value}': ${err}`);
        console.error(err);
      }
    );
  }

}
