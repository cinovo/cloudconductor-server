import { Component, Input, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { ServiceDefaultState, Template, TemplateHttpService } from '../util/http/template.http.service';
import { AlertService } from '../util/alert/alert.service';

interface AutoStartService {
  service: string;
  template: string;
  autostart: boolean;
}

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

  public services: AutoStartService[] = [];

  private servicesToUpdate: AutoStartService[] = [];

  constructor(private templateHttp: TemplateHttpService,
              private alertService: AlertService) {
  }

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

  toggleUpdate(service: AutoStartService): void {
    const index = this.servicesToUpdate.findIndex((a) => a.service == service.service && a.template == service.template);
    if (index > -1) {
      this.servicesToUpdate.splice(index);
    } else {
      this.servicesToUpdate.push(service);
    }
  }

  public updateDefaultStates(): void {
    const updateOps: Observable<ServiceDefaultState>[] = this.servicesToUpdate.map(s => {
      return this.templateHttp.saveServiceDefaultState(this._templateName, s.service, s.autostart ? 'STARTED' : 'STOPPED');
    });
    this.servicesToUpdate = [];
    Observable.forkJoin(updateOps).subscribe(
      () => this.alertService.success(`Successfully updated default service states for template '${this._templateName}'.`),
      (err) => {
        this.alertService.danger(`Error updating default service states for template '${this._templateName}!'`)
        console.error(err);
      }
    );
  }

}
