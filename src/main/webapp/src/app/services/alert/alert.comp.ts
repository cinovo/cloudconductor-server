/**
 * Created by psigloch on 03.11.2016.
 */
import { Component, ViewEncapsulation } from "@angular/core";
import { AlertService } from "./alert.service";

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
