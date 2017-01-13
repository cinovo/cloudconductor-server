import { Component, AfterViewInit } from "@angular/core";
import { PackageServerGroupHttpService, PackageServerGroup } from "../services/http/packageservergroup.http.service";
import { PackageServerHttpService } from "../services/http/packageserver.http.service";
import { ActivatedRoute, Router } from "@angular/router";
import { Observable } from "rxjs";
import { AlertService } from "../services/alert/alert.service";
import { Validator } from "../util/validator.util";
/**
 * Created by psigloch on 09.01.2017.
 */

@Component({
  moduleId: module.id,
  selector: 'psg-edit',
  templateUrl: 'html/psg.edit.comp.html'
})
export class PackageServerGroupEdit implements AfterViewInit {

  private mode: string = "edit";
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
    });
  }

  private loadGroup(groupId: string): void {
    if (Validator.notEmpty(groupId) && +groupId > 0) {
      this.groupHttp.getGroup(groupId).subscribe((result) => {
        this.group = result;
      });
    }
  }

  save(): void {
    this.doSave(() => {
      this.alerts.success("Successfully saved your package server group");
      if (this.mode == "new") {
        this.router.navigate(["packageservers"]);
      }
    });
  }

  addServer(): void {
    this.doSave(() => {
      this.router.navigate(["packageservers", this.group.id, "server", "new"]);
    });
  }

  editServer(id: number): void {
    this.doSave(() => this.router.navigate(["packageservers", this.group.id, "server", id]));
  }

  deleteServer(id: number): void {
    if (Validator.idIsSet(id)) {
      this.serverHttp.deletePackageServer(id.toString()).subscribe(
        () => {
          this.group.packageServers.forEach(function (item, index, object) {
            if (item.id === id) {
              object.splice(index, 1);
            }
          });
        });
    }
  }

  setPrimary(id: number): void {
    this.group.primaryServer = id;
  }

  private fieldValidation(): boolean {
    let error = false;
    if (!this.isNameValid()) {
      this.alerts.danger("Please insert a group name.");
      error = true;
    }
    return error;
  }

  private isNameValid(): boolean {
    return Validator.notEmpty(this.group.name);
  }

  private doSave(successCallback: (psg: PackageServerGroup) => any): void {
    if (this.fieldValidation()) {
      return;
    }
    let call: Observable<PackageServerGroup>;
    if (Validator.idIsSet(this.group.id)) {
      call = this.groupHttp.editGroup(this.group);
    } else {
      call = this.groupHttp.newGroup(this.group);
    }
    call.subscribe(
      (result) => {
        this.group = result;
        if (successCallback) {
          successCallback(result);
        }
      },
      (error) => {
        this.alerts.danger("Failed to save the package server group");
        console.log(error);
      }
    );
  }
}
