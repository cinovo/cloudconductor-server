import { Component, Input, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { Template, TemplateHttpService, ServiceDefaultState } from '../util/http/template.http.service';
import { Service } from '../util/http/service.http.service';
import { AlertService } from '../util/alert/alert.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'template-services',
  templateUrl: './template.service.comp.html'
})
export class TemplateServiceComponent implements OnInit {

  @Input() obsTemplate: Observable<Template>;

  private _templateName: string;

  public services: {service: string, template: string, autostart: boolean}[];

  constructor(private templateHttp: TemplateHttpService,
              private alertService: AlertService) { }

  ngOnInit(): void {
    this.obsTemplate.subscribe(
      (template) => {
        if (template && template.name && template.name.length > 0) {
          this._templateName = template.name;
          this.loadServices(this._templateName);
        }
      }
    );
  }

  private loadServices(templateName: string) {
    const services$ = this.templateHttp.getServicesForTemplate(templateName);
    const serviceDss$ = this.templateHttp.getServiceDefaultStates(templateName);

    // get all services for template and merge information with service default states
    Observable.forkJoin(services$, serviceDss$).subscribe(
      ([services, dss]) => {
        this.services = services.map(service => {
          const autostart = dss.some(state => state.service === service.name && state.state === 'STARTED');
          return {service: service.name, template: templateName, autostart: autostart};
        });
      }, (err) => {
        this.alertService.danger(`Error loading services for template '${templateName}'!`);
        console.error(err);
      }
    );
  }

  public updateDefaultStates(): void {
    const updateOps: Observable<ServiceDefaultState>[] = this.services.map(s => {
      const state = s.autostart ? 'STARTED' : 'STOPPED';
      return this.templateHttp.saveServiceDefaultState(this._templateName, s.service, state);
    });

    Observable.forkJoin(updateOps).subscribe(
      () => this.alertService.success(`Successfully updated default service states for template '${this._templateName}'.`),
      (err) => {
        this.alertService.danger(`Error updating default service states for template '${this._templateName}!'`)
        console.error(err);
      }
    );
  }

}
