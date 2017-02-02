import { Component, AfterViewInit, Input, Output, EventEmitter } from "@angular/core";
import { Template, TemplateHttpService, AgentOption } from "../services/http/template.http.service";
import { Observable } from "rxjs";
import { AlertService } from "../services/alert/alert.service";
import { Validator } from "../util/validator.util";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  moduleId: module.id,
  selector: 'template-agentoptions',
  templateUrl: 'html/template.agentoption.comp.html'
})
export class TemplateAgentOptions implements AfterViewInit {

  @Input() obsTemplate: Observable<Template>;
  @Output() reloadTrigger: EventEmitter<any> = new EventEmitter();
  private options: AgentOption = {};

  private noGtEqZero = Validator.noGtEqZero;

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

  protected saveOptions(): void {
    this.templateHttp.saveAgentOptions(this.options).subscribe(
      (result) => {
        this.options = result;
        this.alerts.success("Successfully saved the agent behaviour.")
      },
      () => this.alerts.danger("Failed to save the agent behaviour.")
    )
  }
}
