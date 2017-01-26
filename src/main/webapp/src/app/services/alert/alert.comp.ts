import { Component, ViewEncapsulation } from "@angular/core";
import { AlertService } from "./alert.service";
/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  moduleId: module.id,
  selector: 'alert-area',
  templateUrl: 'html/alert.comp.html',
  styleUrls: ["css/alert.comp.css"]
})
export class AlertComponent {

  constructor(private alertService: AlertService) {
  };

}
