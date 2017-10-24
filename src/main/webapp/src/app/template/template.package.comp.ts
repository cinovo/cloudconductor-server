import { Component, Input, AfterViewInit, Output, EventEmitter } from '@angular/core';

import { Observable } from 'rxjs';

import { Template, TemplateHttpService } from '../util/http/template.http.service';
import { PackageHttpService, PackageVersion } from '../util/http/package.http.service';
import { Sorter } from '../util/sorters.util';
import { Validator } from '../util/validator.util';
import { AlertService } from '../util/alert/alert.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
interface TemplatePackageVersion {
  pkg: string,
  version: string,
  selected: boolean
}

type PackageTree = {
  [pkgName: string]: {
    pkgName: string,
    inUse: boolean,
    versions: Array<PackageVersion>,
    newestVersion?: PackageVersion
  }
}

@Component({
  selector: 'template-packages',
  templateUrl: './template.package.comp.html'
})
export class TemplatePackages implements AfterViewInit {

  @Input() obsTemplate: Observable<Template>;

  private template: Template = {name: '', description: ''};
  public packageVersions: Array<TemplatePackageVersion> = [];

  public packageTree: PackageTree = {};
  public newPackage: {pkg: string, version: string} = null;

  private _allSelected = false;

  constructor(private packageHttp: PackageHttpService,
              private templateHttp: TemplateHttpService,
              private alerts: AlertService) { };

  ngAfterViewInit(): void {
    this.obsTemplate.subscribe((result) => {
      return this.load(result);
    });
  }

  private load(template: Template): void {
    if (!template) {
      return;
    }
    this.template = template;
    this.loadVersions();
    this.cancelAddPackage();
    this.packageTree = {};

    if (this.template.repos) {
      for (let repo of this.template.repos) {
        this.packageHttp.getVersionsOfRepo(repo).subscribe((result) => {
           this.preparePVS(result);
        });
      }
    }
  }

  private loadVersions(): void {
    let oldPackageVersions = this.packageVersions;
    this.packageVersions = [];
    for (let key in this.template.versions) {
      if (this.template.versions.hasOwnProperty(key)) {
        let selected = this.allSelected;
        if (!selected) {
          for (let old of oldPackageVersions) {
            if (old.pkg === key) {
              selected = old.selected;
              break;
            }
          }
        }
        this.packageVersions.push({pkg: key, version: this.template.versions[key], selected: selected});
      }
    }
    /* tslint:disable:curly */
    this.packageVersions.sort((a: TemplatePackageVersion, b: TemplatePackageVersion) => {
      if (a.pkg < b.pkg) return -1;
      if (a.pkg > b.pkg) return 1;
      return 0;
    });
  }

  private preparePVS(pvs: Array<PackageVersion>) {
    for (let pv of pvs) {
      if (!this.packageTree[pv.name]) {
        this.packageTree[pv.name] = {pkgName: pv.name, inUse: this.templateContainsPackage(pv), versions: []};
      }
      let entry: any = this.packageTree[pv.name];
      if (entry.versions.indexOf(pv) < 0) {
        entry.versions.push(pv);
        entry.versions.sort(Sorter.packageVersion);
        if (Sorter.packageVersion(entry.newestVersion, pv) < 0) {
          entry.newestVersion = pv;
        }
      }
    }
  }


  protected updatePackage(pv: TemplatePackageVersion): void {
    if (pv) {
      this.templateHttp.updatePackage(this.template, pv.pkg).subscribe(
        () => {
          this.alerts.success('The package ' + pv.pkg + ' has been successfully updated.');
        },
        (error) => this.alerts.danger('The package update of ' + pv.pkg + ' failed.')
      )
    }
  }

  private updateSelected(index = 0): void {
    while (index < this.packageVersions.length && !this.packageVersions[index].selected) {
      index++;
    }
    if (index >= this.packageVersions.length) {
      this.allSelected = false;
      return;
    }

    if (this.isPkgLatest(this.packageVersions[index])) {
      this.updateSelected(index + 1);
      return;
    }
    this.templateHttp.updatePackage(this.template, this.packageVersions[index].pkg).subscribe(
      () => {
        this.alerts.success('The package ' + this.packageVersions[index].pkg + ' has been successfully updated.');
        this.updateSelected(index + 1);
      },
      () => {
        this.alerts.danger('The package update of ' + this.packageVersions[index].pkg + ' failed.');
        this.updateSelected(index + 1);
      }
    )
  }

  protected removePackage(pv: TemplatePackageVersion) {
    if (pv) {
      this.templateHttp.deletePackage(this.template, pv.pkg).subscribe(
        () => {
          this.alerts.success('The package ' + pv.pkg + ' has been successfully removed.');
        },
        () => this.alerts.danger('The package removal of ' + pv.pkg + ' failed.')
      )
    }
  }

  private removeSelected(index = 0): void {
    while (index < this.packageVersions.length && !this.packageVersions[index].selected) {
      index++;
    }
    if (index >= this.packageVersions.length) {
      this.allSelected = false;
      return;
    }

    this.templateHttp.deletePackage(this.template, this.packageVersions[index].pkg).subscribe(
      () => {
        this.alerts.success('The package ' + this.packageVersions[index].pkg + ' has been successfully removed.');
        this.removeSelected(index + 1);
      },
      () => {
        this.alerts.danger('The package removal of ' + this.packageVersions[index].pkg + ' failed.');
        this.removeSelected(index + 1);
      }
    )
  }


  get allSelected(): boolean {
    return this._allSelected;
  }

  set allSelected(value: boolean) {
    this._allSelected = value;
    for (let pv of this.packageVersions) {
      pv.selected = this._allSelected;
    }
  }

  protected pkgSelect(pv: TemplatePackageVersion, event: any) {
    let index = this.packageVersions.indexOf(pv);
    if (index > -1) {
      this.packageVersions[index].selected = event.target.checked;
    }
  }

  protected isPkgSelected(): boolean {
    for (let pv of this.packageVersions) {
      if (pv.selected) {
        return true;
      }
    }
    return false;
  }

  private isPkgLatest(pv: TemplatePackageVersion) {
    return this.packageTree[pv.pkg] && this.packageTree[pv.pkg].newestVersion.version === pv.version;
  }

  protected goToAddPackage(): void {
    this.newPackage = {pkg: '', version: ''};
  }

  protected addNewPackage(): void {
    if (this.newPackage) {
      this.templateHttp.getTemplate(this.template.name).subscribe(
        (result) => {
          result.versions[this.newPackage.pkg] = this.newPackage.version;
          this.templateHttp.save(result).subscribe(
            () => {
              this.alerts.success('Successfully added the package to the template.');
              this.newPackage = null;
            },
            () => {
              this.alerts.danger('Failed to add the new package to the template.');
              this.cancelAddPackage();
            }
          )
        },
        () => {
          this.alerts.danger('Failed to add the new package to the template.');
          this.cancelAddPackage();
        }
      )
    }
  }

  private getVersions(pkgName: string): Array<PackageVersion> {
    if (Validator.notEmpty(pkgName)) {
      return this.packageTree[pkgName].versions.sort(Sorter.packageVersion);
    }
    return [];
  }

  protected onPackageChange(): void {
    let versions = this.getVersions(this.newPackage.pkg);
    if (versions.length > 0) {
      this.newPackage.version = versions[versions.length - 1].version;
    } else {
      this.newPackage.version = '';
    }
  }

  private cancelAddPackage(): void {
    this.newPackage = null;
  }

  protected packageTreeArray(): Array<any> {
    let result = [];
    for (let element of Object.values(this.packageTree)) {
      if (this.template.versions[element.pkgName] == null) {
        result.push(element);
      }
    }
    return result.sort((a, b) => Sorter.byField(a, b, 'pkgName'));
  }

  private templateContainsPackage(pv: PackageVersion): boolean {
    for (let name in this.template.versions) {
      if (name === pv.name) {
        return true;
      }
    }
    return false;
  }
}

