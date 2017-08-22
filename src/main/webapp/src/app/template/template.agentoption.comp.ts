import { Component, AfterViewInit, Input, Output, EventEmitter } from '@angular/core';

import { Observable } from 'rxjs';

import { Template, TemplateHttpService, AgentOption } from '../util/http/template.http.service';
import { AlertService } from '../util/alert/alert.service';
import { Validator } from '../util/validator.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'template-agentoptions',
  templateUrl: './template.agentoption.comp.html'
})
export class TemplateAgentOptions implements AfterViewInit {

  @Input() obsTemplate: Observable<Template>;
  @Output() reloadTrigger: EventEmitter<any> = new EventEmitter();
  public options: AgentOption = {};
  public noGtEqZero = Validator.noGtEqZero;

  constructor(private templateHttp: TemplateHttpService,
              private alerts: AlertService) {
  };

  ngAfterViewInit(): void {
    this.obsTemplate.subscribe(
      (result) => this.loadOptions(result)
    )
  }

  private loadOptions(template: Template): void {
    if (template.name) {
      this.templateHttp.loadAgentOptions(template.name).subscribe(
        (result) => {
          this.options = result;
        }
      )
    }
  }

  public saveOptions(): void {
    this.templateHttp.saveAgentOptions(this.options).subscribe(
      (result) => {
        this.options = result;
        this.alerts.success('Successfully saved the agent behaviour.')
      },
      () => this.alerts.danger('Failed to save the agent behaviour.')
    )
  }

}
