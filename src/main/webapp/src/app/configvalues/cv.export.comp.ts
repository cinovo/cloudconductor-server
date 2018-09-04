import { DOCUMENT } from "@angular/common";
import { Component, Inject, OnDestroy, OnInit } from "@angular/core";
import { ConfigValue, ConfigValueHttpService } from "../util/http/configValue.http.service";
import { ActivatedRoute } from "@angular/router";
import { Subscription } from "rxjs/Subscription";
import { AlertService } from "../util/alert/alert.service";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cs.export.comp',
  templateUrl: './cv.export.comp.html'
})
export class ConfigValueExport implements OnInit, OnDestroy {

  private routeSub: Subscription;
  public template: string;
  public export: ConfigValue[];

  constructor(private configHttp: ConfigValueHttpService, private route: ActivatedRoute, private alerts: AlertService, @Inject(DOCUMENT) private dom: Document) {
  }

  ngOnInit(): void {
    this.routeSub = this.route.paramMap.subscribe((paramMap) => {
      this.template = paramMap.get('template');
      this.loadData();
    });
  }

  ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }

  private loadData() {
    this.configHttp.getValues(this.template).subscribe((result) => {
      this.export = result;
    }, (err) => {
      this.alerts.danger(`Error loading config values for template '${this.template}'!`);
    });
  }


}
