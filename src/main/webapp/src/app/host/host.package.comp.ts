import { Subscription } from 'rxjs/Rx';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';

import { Observable } from 'rxjs';

import { Host } from '../util/http/host.http.service';
import { SettingHttpService } from '../util/http/setting.http.service';
import { Sorter } from '../util/sorters.util';
import { TemplateHttpService } from '../util/http/template.http.service';

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
    this.hostSub = this.obsHost.subscribe(
      (result) => {
        this.loadPackages(result);
        this.host = result;
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
        this.uninstallDisallowed = unDis;
        return this.templateHttp.getTemplate(host.template);
      }).subscribe(
        (template) => {
          for (let index of Object.keys(host.packages)) {
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

  private updateState(element: {name: string; hostVersion: string; templateVersion: string; state: string}) {
    if (!element.templateVersion && !(this.uninstallDisallowed.indexOf(element.name) > -1)) {
      element['state'] = 'uninstalling';
      this.packageChanges = true;
    } else if (!element.hostVersion) {
      element['state'] = 'installing';
      this.packageChanges = true;
    } else {
      let comp = Sorter.versionComp(element.hostVersion, element.templateVersion);
      if (comp < 0) {
        element['state'] = 'upgrading';
        this.packageChanges = true;
      }
      if (comp > 0) {
        element['state'] = 'downgrading';
        this.packageChanges = true;
      }
    }
    return element;
  }

}
