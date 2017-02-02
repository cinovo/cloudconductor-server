/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { Component, AfterViewInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { Host, HostHttpService } from "../services/http/host.http.service";
import { AlertService } from "../services/alert/alert.service";
import { BehaviorSubject, Observable } from "rxjs";
import { Validator } from "../util/validator.util";


@Component({
  moduleId: module.id,
  selector: 'host.detail.comp',
  templateUrl: 'html/host.detail.comp.html'
})
export class HostDetail implements AfterViewInit {
  private _behavHost: BehaviorSubject<Host> = new BehaviorSubject({name: "", template: ""});
  public obsHost: Observable<Host> = this._behavHost.asObservable();
  private host: Host = {name: "", template: ""};

  private back: {ret: string; id: string};
  private autorefresh: boolean = false;

  constructor(private route: ActivatedRoute,
              private hostHttp: HostHttpService,
              private alerts: AlertService) {
  };

  ngAfterViewInit(): void {
    this.route.params.subscribe((params) => {
      this.loadData(params['hostName']);
    });
    this.obsHost.subscribe(
      (result) => this.host = result
    );

    this.route.queryParams.subscribe((params) => {
      this.back = {ret: params['ret'], id: params['id']};
    });
  }


  private loadData(hostName: string): void {
    if (Validator.notEmpty(hostName)) {
      this.hostHttp.getHost(hostName).subscribe(
        (result) => {
          this._behavHost.next(result);
        },
        (error) => this.alerts.danger("The host does not exist!")
      )
    }
  }

  private reloadHost(): void {
    this.loadData(this.host.name);
  }

}
