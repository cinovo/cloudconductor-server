import { Component, Input, OnDestroy, OnInit } from '@angular/core';

import { forkJoin, Observable, Subscription } from 'rxjs';

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
export class TemplateServiceComponent implements OnInit, OnDestroy {

  @Input() obsTemplate: Observable<Template>;

  private _templateName: string;
  private templateSub: Subscription;

  public services: AutoStartService[] = [];

  private servicesToUpdate: AutoStartService[] = [];

  constructor(private readonly templateHttp: TemplateHttpService,
              private readonly alertService: AlertService) { }

  ngOnInit(): void {
    this.templateSub = this.obsTemplate.subscribe(
      (template) => {
        if (template && template.name && template.name.length > 0) {
          this._templateName = template.name;
          this.loadServices(this._templateName);
        }
      }
    );
  }


  ngOnDestroy(): void {
    if (this.templateSub) {
      this.templateSub.unsubscribe();
    }
  }

  private loadServices(templateName: string) {
    const services$ = this.templateHttp.getServicesForTemplate(templateName);
    const serviceDss$ = this.templateHttp.getServiceDefaultStates(templateName);

    // get all services for template and merge information with service default states
    forkJoin([services$, serviceDss$]).subscribe(
      ([services, dss]) => {
        this.services = services.map(service => {
          const autostart = dss.some(state => state.service === service.name && state.state === 'STARTED');
          return { service: service.name, template: templateName, autostart: autostart };
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
      // immutable splice
      this.servicesToUpdate = [...this.servicesToUpdate.slice(0, index), ...this.servicesToUpdate.slice(index + 1)];
    } else {
      this.servicesToUpdate = [...this.servicesToUpdate, service];
    }
  }

  public updateDefaultStates(): void {
    const updateOps: Observable<ServiceDefaultState>[] = this.servicesToUpdate.map(s => {
      return this.templateHttp.saveServiceDefaultState(this._templateName, s.service, s.autostart ? 'STARTED' : 'STOPPED');
    });

    forkJoin(updateOps).subscribe(
      () => {
        this.servicesToUpdate = [];
        this.alertService.success(`Successfully updated default service states for template '${this._templateName}'.`);
      }, (err) => {
        this.alertService.danger(`Error updating default service states for template '${this._templateName}'!`);
        console.error(err);
      }
    );
  }

}
