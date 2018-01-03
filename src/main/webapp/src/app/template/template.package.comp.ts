import { Component, Input, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

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
export interface TemplatePackageVersion {
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
export class TemplatePackages implements OnInit, OnDestroy {

  @Input() obsTemplate: Observable<Template>;

  private template: Template = {name: '', description: ''};
  public packageVersions: TemplatePackageVersion[] = [];

  public packageTree: PackageTree = {};
  public newPackage: {pkg: string, version: string} = null;

  private _allSelected = false;
  private templateSub: Subscription;

  constructor(private packageHttp: PackageHttpService,
              private templateHttp: TemplateHttpService,
              private alerts: AlertService) { };

  ngOnInit(): void {
    this.templateSub = this.obsTemplate.subscribe((template) => {
      return this.load(template);
    });
  }

  ngOnDestroy(): void {
    if (this.templateSub) {
      this.templateSub.unsubscribe();
    }
  }

  private load(template: Template) {
    if (!template) {
      return;
    }
    this.template = template;
    this.loadVersions();
    this.cancelAddPackage();

    if (this.template.repos) {
      const repoOps: Observable<PackageVersion[]>[] = this.template.repos.map(repo => this.packageHttp.getVersionsOfRepo(repo));

      Observable.forkJoin(repoOps).subscribe((results) => {
        const flattetResults = results.reduce((flat, toFlatten) => {
          return flat.concat(toFlatten);
        }, []);

        this.packageTree = this.preparePVS(flattetResults);
      });
    }
  }

  private loadVersions(): void {
    const newPackageVersions = [];
    for (let key in this.template.versions) {
      if (this.template.versions.hasOwnProperty(key)) {
        let selected = this.allSelected;
        if (!selected) {
          for (let old of this.packageVersions) {
            if (old.pkg === key) {
              selected = old.selected;
              break;
            }
          }
        }
        newPackageVersions.push({pkg: key, version: this.template.versions[key], selected: selected});
      }
    }
    newPackageVersions.sort(Sorter.templatePackageVersion);

    this.packageVersions = newPackageVersions;
  }

  private preparePVS(pvs: PackageVersion[]) {
    const newPVTree = {}
    for (let pv of pvs) {
      if (!newPVTree[pv.name]) {
        newPVTree[pv.name] = {pkgName: pv.name, inUse: this.templateContainsPackage(pv), versions: []};
      }
      let entry = newPVTree[pv.name];
      if (entry.versions.findIndex((version) => version.name === pv.name && version.version === pv.version) < 0) {
        entry.versions.push(pv);
        entry.versions.sort(Sorter.packageVersion);
        if (Sorter.packageVersion(entry.newestVersion, pv) < 0) {
          entry.newestVersion = pv;
        }
      }
    }

    return newPVTree;
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
    const pkgToUpdate = Object.assign({}, this.packageVersions[index]);
    this.templateHttp.updatePackage(this.template, pkgToUpdate.pkg).subscribe(
      () => {
        this.updateSelected(index + 1);
      },
      (err) => {
        this.alerts.danger(`Error updating package '${pkgToUpdate.pkg}'!`);
        console.error(err);
        this.updateSelected(index + 1);
      }
    );
  }

  protected removePackage(pv: TemplatePackageVersion) {
    if (pv) {
      this.templateHttp.deletePackage(this.template, pv.pkg).subscribe(
        () => {
          this.alerts.success(`Package '${pv.pkg}' has been removed successfully.`);
        },
        (err) => {
          this.alerts.danger(`Error removing package '${pv.pkg}'!`);
          console.error(err);
        }
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

    const pvToRemove = Object.assign({}, this.packageVersions[index]);
    this.templateHttp.deletePackage(this.template, pvToRemove.pkg).subscribe(
      () => {
        this.removeSelected(index);
      },
      (err) => {
        this.alerts.danger(`Error removing package '${pvToRemove.pkg}'!`);
        console.error(err);
        this.removeSelected(index + 1);
      }
    );
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

  protected selectPackage(pv: TemplatePackageVersion, event: any) {
    let index = this.packageVersions.indexOf(pv);
    if (index > -1) {
      this.packageVersions[index].selected = event.target.checked;
    }
  }

  protected isPkgSelected(): boolean {
    return this.packageVersions.some(pv => pv.selected);
  }

  private isPkgLatest(pv: TemplatePackageVersion) {
    return this.packageTree[pv.pkg] && this.packageTree[pv.pkg].newestVersion.version === pv.version;
  }

  protected goToAddPackage(): void {
    this.newPackage = {pkg: '', version: ''};
  }

  protected addNewPackage(): void {
    if (this.newPackage && this.newPackage.pkg && this.newPackage.version) {
      const addedPkg = Object.assign({}, this.newPackage);
      this.templateHttp.getTemplate(this.template.name).subscribe(
        (result) => {
          result.versions[addedPkg.pkg] = addedPkg.version;
          this.templateHttp.save(result).subscribe(
            () => {
              this.alerts.success(`Successfully added package '${addedPkg.pkg}:${addedPkg.version}' to the template.`);
              this.newPackage = null;
            },
            () => {
              this.alerts.danger(`Failed to add package '${addedPkg.pkg}:${addedPkg.version}' to the template!`);
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
      return [...this.packageTree[pkgName].versions].sort(Sorter.packageVersion);
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

