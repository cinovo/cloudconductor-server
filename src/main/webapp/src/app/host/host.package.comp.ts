import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';

import { Observable, Subscription } from 'rxjs/Rx';

import { Host } from '../util/http/host.http.service';
import { SettingHttpService } from '../util/http/setting.http.service';
import { Sorter } from '../util/sorters.util';
import { TemplateHttpService } from '../util/http/template.http.service';

interface PackageChange {
  name: string;
  hostVersion: string;
  templateVersion: string;
  state: string
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'host-packages',
  templateUrl: './host.package.comp.html'
})
export class HostPackages implements OnInit, OnDestroy {

  @Input() obsHost: Observable<Host>;
  @Output() reloadTrigger: EventEmitter<any> = new EventEmitter();

  public packages: Array<{name: string, hostVersion: string, templateVersion?: string, state?: string}> = [];
  public packageChanges = false;
  public host: Host = {name: '', template: ''};

  private hostSub: Subscription;
  private uninstallDisallowed: string[] = [];

  constructor(private templateHttp: TemplateHttpService,
              private settingHttp: SettingHttpService) { };

  public ngOnInit(): void {
    this.hostSub = this.obsHost.subscribe((newHost) => {
        this.loadPackages(newHost);
        this.host = newHost;
      });
  }

  public ngOnDestroy(): void {
    if (this.hostSub) {
      this.hostSub.unsubscribe();
    }
  }

  private loadPackages(host: Host): void {
    this.packages = [];
    this.packageChanges = false;

    if (host && host.packages && Object.keys(host.packages).length > 0) {
      this.settingHttp.getNoUninstall().flatMap((unDis: string[]) => {
        // first retrieve list of packages which are not allowed to uninstall
        this.uninstallDisallowed = unDis;

        // second retrieve list of packages which SHOULD be installed according to the template
        return this.templateHttp.getTemplate(host.template);
      }).subscribe((template) => {
          const allPackages = Object.assign({}, template.versions, host.packages);
          for (let index of Object.keys(allPackages)) {
            let element = {
              name: index,
              hostVersion: host.packages[index],
              templateVersion: template.versions[index],
              state: 'ok'
            };
            element = this.updateState(element);
            this.packages.push(element);
          }
          this.packages.sort(Sorter.nameField);
        }
      );
    }
  }

  private updateState(element: PackageChange) {
    if (!element.templateVersion) {
      if (!(this.uninstallDisallowed.indexOf(element.name) > -1)) {
        element.state = 'uninstalling';
        this.packageChanges = true;
      } else {
        element.state = 'protected';
      }
    } else if (!element.hostVersion) {
      element.state = 'installing';
      this.packageChanges = true;
    } else {
      let comp = Sorter.versionComp(element.hostVersion, element.templateVersion);
      if (comp < 0) {
        element.state = 'upgrading';
        this.packageChanges = true;
      }
      if (comp > 0) {
        element.state = 'downgrading';
        this.packageChanges = true;
      }
    }
    return element;
  }

}
