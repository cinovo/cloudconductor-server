/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { Component, Input, EventEmitter, Output } from "@angular/core";
import { Observable } from "rxjs";
import { TemplateHttpService } from "../util/http/template.http.service";
import { Host } from "../util/http/host.http.service";
import { Sorter } from "../util/sorters.util";

@Component({
  moduleId: module.id,
  selector: 'host-packages',
  templateUrl: 'html/host.package.comp.html'
})
export class HostPackages {
  @Input() obsHost: Observable<Host>;
  @Output() reloadTrigger: EventEmitter<any> = new EventEmitter();


  private packages: Array<{name: string, hostVersion: string, templateVersion?: string, state?: string}> = [];
  private packageChanges: boolean = false;
  private host: Host = {name: "", template: ""};

  constructor(private templateHttp: TemplateHttpService) {
  };

  ngAfterViewInit(): void {
    this.obsHost.subscribe(
      (result) => {
        this.loadPackages(result);
        this.host = result;
      }
    );
  }

  private loadPackages(host: Host): void {
    this.packages = [];
    this.packageChanges = false;
    if (host && host.packages && Object.keys(host.packages).length > 0) {
      this.templateHttp.getTemplate(host.template).subscribe(
        (result) => {
          for (let index of Object.keys(host.packages)) {
            let element = {
              name: index,
              hostVersion: host.packages[index],
              templateVersion: result.versions[index],
              state: "ok"
            };
            element = this.updateState(element);
            this.packages.push(element);
          }
          this.packages.sort(Sorter.nameField);
        }
      )
    }
  }

  private updateState(element: {name: string; hostVersion: string; templateVersion: string; state: string}) {
    if (!element.templateVersion) {
      element['state'] = "uninstalling";
      this.packageChanges = true;
    } else if (!element.hostVersion) {
      element['state'] = "installing";
      this.packageChanges = true;
    } else {
      let comp = Sorter.versionComp(element.hostVersion, element.templateVersion);
      if (comp < 0) {
        element['state'] = "upgrading";
        this.packageChanges = true;
      }
      if (comp > 0) {
        element['state'] = "downgrading";
        this.packageChanges = true;
      }
    }
    return element;
  }
}
