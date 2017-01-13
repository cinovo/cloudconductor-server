/**
 * Created by psigloch on 03.11.2016.
 */
/**
 * Created by psigloch on 28.09.2016.
 */
import { Component } from "@angular/core";
import { ConfigHttpService } from "../services/http/config.http.service";


@Component({
  moduleId: module.id,
  templateUrl: 'html/home.component.html'
})
export class HomeComponent {
  private config: Map<string, string>;
  private errormessage;

  constructor(private as: ConfigHttpService) {
    as.getConfig("GLOBAL").subscribe(
      this.test,
      error => this.errormessage = error.toString());
  };

  private test(value:Map<string, string>) {
    console.log(value);
    this.config = value;
  }
}
