import { Component, OnInit } from "@angular/core";
import { ConfigValueHttpService } from "../util/http/configValue.http.service";
import { ServiceHttpService } from "../util/http/service.http.service";
import { AlertService } from "../util/alert/alert.service";
import { ActivatedRoute, Router } from "@angular/router";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";

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

  constructor(private configHttp: ConfigValueHttpService,
              private route: ActivatedRoute,
              private alerts: AlertService,
              private router: Router,
              private serviceHttp: ServiceHttpService,
              private fb: FormBuilder) {
    this.kvForm = this.fb.group({
      template: ['', [Validators.required]],
      newTemplate: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.configHttp.templates.subscribe((result) => this.templates = result);
  }

  public save() {
    this.configHttp.getValues(this.kvForm.value.template).subscribe((result) => {
      if (result.length < 1) {
        this.alerts.danger(`Error creating new template '${this.kvForm.value.newTemplate}'!`);
        return;
      }
      for (let i = 0; i < result.length; i++) {
        result[i].template = this.kvForm.value.newTemplate;
        if (i == result.length - 1) {
          this.configHttp.save(result[i]).subscribe((result) => {
              this.alerts.success(`Successfully created new template '${this.kvForm.value.newTemplate}' and copied values from '${this.kvForm.value.template}'.`);
              this.router.navigate(['/config', this.kvForm.value.newTemplate]);
            }
          );
        } else {
          this.configHttp.save(result[i]);
        }
      }
    }, (err) => {
      this.alerts.danger(`Error creating new template '${this.kvForm.value.newTemplate}'!`);
    });
  }


}
