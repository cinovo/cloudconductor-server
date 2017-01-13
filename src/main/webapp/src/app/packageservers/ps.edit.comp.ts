import { Component, AfterViewInit } from "@angular/core";
import { PackageServerGroupHttpService, PackageServerGroup } from "../services/http/packageservergroup.http.service";
import { ActivatedRoute, Router } from "@angular/router";
import { AlertService } from "../services/alert/alert.service";
import { PackageServerHttpService, PackageServer } from "../services/http/packageserver.http.service";
import { Observable } from "rxjs";
import { Validator } from "../util/validator.util";

/**
 * Created by psigloch on 10.01.2017.
 */
@Component({
  moduleId: module.id,
  selector: 'ps-edit',
  templateUrl: 'html/ps.edit.comp.html'
})
export class PackageServerEdit implements AfterViewInit {

  private mode: string = "edit";
  private returnToOverview: boolean = false;
  private server: PackageServer = {
    path: "",
    basePath: "",
    description: "",
    indexerType: "NONE",
    providerType: "NONE",
    serverGroup: -1
  };

  private group: PackageServerGroup = {name: "", packageServers: [], primaryServer: 0};

  constructor(private groupHttp: PackageServerGroupHttpService, private serverHttp: PackageServerHttpService,
              private route: ActivatedRoute, private alerts: AlertService, private router: Router) {
  };

  ngAfterViewInit(): void {
    this.route.url.subscribe((value) => {
        if (value[value.length - 1].path == 'new') {
          this.mode = 'new'
        }
      }
    );
    this.route.params.subscribe((params) => {
      this.loadGroup(params['id']);
      this.server.serverGroup = params['id'];
      this.loadServer(params['serverid']);
    });

    this.route.queryParams.subscribe((params) => {
      if (params['ret'] && params['ret'] == "ov") {
        this.returnToOverview = true;
      }
    });
  }


  save(): void {
    if (this.fieldValidation()) {
      return;
    }
    let call: Observable<PackageServer>;
    if (Validator.idIsSet(this.server.id)) {
      call = this.serverHttp.editPackageServer(this.server);
    } else {
      call = this.serverHttp.newPackageServer(this.server);
    }
    call.subscribe(
      (result) => {
        this.server = result;
        if (this.returnToOverview) {
          this.router.navigate(["packageservers"]);
        } else {
          this.router.navigate(["packageservers", this.group.id])
        }
      },
      (error) => {
        this.alerts.danger("Failed to save the package server");
        console.log(error);
      }
    );
  }

  private fieldValidation() {
    let error = false;
    if (this.isDescriptionValid()) {
      this.alerts.danger("Please insert a description.");
      error = true;
    }
    if (this.isPathValid()) {
      this.alerts.danger("Please insert a path.");
      error = true;
    }
    return error;
  }

  private isDescriptionValid(): boolean {
    return Validator.notEmpty(this.server.description);
  }

  private isPathValid(): boolean {
    return Validator.notEmpty(this.server.path);
  }

  private loadGroup(groupId: string): void {
    if (Validator.notEmpty(groupId) && +groupId > 0
    ) {
      this.groupHttp.getGroup(groupId).subscribe((result) => {
        this.group = result;
      });
    }
  }

  private loadServer(serverid: any) {
    if (Validator.notEmpty(serverid) && +serverid > 0) {
      this.serverHttp.getPackageServer(serverid).subscribe((result) => {
        this.server = result;
      });
    }
  }
}
