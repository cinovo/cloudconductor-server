import { Sorter } from '../sorters.util';
import { TemplateHttpService } from '../http/template.http.service';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { SettingHttpService } from '../http/setting.http.service';

export interface PackageChange {
  name: string;
  hostVersion: string;
  templateVersion: string;
  state: string
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class PackageChangesService {

  private uninstallDisallowed: string[] = [];

  constructor(private settingHttp: SettingHttpService,
              private templateHttp: TemplateHttpService) { }

  public computePackageChanges(host): Observable<PackageChange[]> {

    return this.settingHttp.getNoUninstall().flatMap((unDis: string[]) => {
      // first retrieve list of packages which are not allowed to uninstall
      this.uninstallDisallowed = unDis;

      // second retrieve list of packages which SHOULD be installed according to the template
      return this.templateHttp.getTemplate(host.template);
    }).flatMap((template) => {
      const packageChanges: PackageChange[] = [];
      const allPackages = Object.assign({}, template.versions, host.packages);
      for (let index of Object.keys(allPackages)) {
        let element = {
          name: index,
          hostVersion: host.packages[index],
          templateVersion: template.versions[index],
          state: 'ok'
        };
        element = this.updateState(element);
        packageChanges.push(element);
      }
      packageChanges.sort(Sorter.nameField);

      return Observable.of(packageChanges)
    });
  }

  private updateState(element: PackageChange) {
    if (!element.templateVersion) {
      // host has package which is not in template
      if (!(this.uninstallDisallowed.indexOf(element.name) > -1)) {
        element.state = 'uninstalling';
      } else {
        element.state = 'protected';
      }
    } else if (!element.hostVersion) {

      // package is missing on host
      element.state = 'installing';
    } else {

      // package installed but versions differ between host and template
      let comp = Sorter.versionComp(element.hostVersion, element.templateVersion);
      if (comp < 0) {
        element.state = 'upgrading';
      }
      if (comp > 0) {
        element.state = 'downgrading';
      }
    }
    return element;
  }

}
