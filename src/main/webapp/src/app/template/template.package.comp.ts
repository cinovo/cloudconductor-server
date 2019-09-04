import { Component, Input, OnDestroy, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { saveAs } from "file-saver";

import { Template, TemplateHttpService } from '../util/http/template.http.service';
import { PackageHttpService, PackageVersion, SimplePackageVersion } from '../util/http/package.http.service';
import { Sorter } from '../util/sorters.util';
import { AlertService } from '../util/alert/alert.service';
import { Validator } from '../util/validator.util';
import { DatePipe } from "@angular/common";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch, mweise
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
  public newPackage: { pkg: string, version: string } = null;

  public importing = false;

  private _allSelected = false;
  private templateSub: Subscription;

  constructor(private packageHttp: PackageHttpService,
              private templateHttp: TemplateHttpService,
              private alerts: AlertService,
              private datePipe: DatePipe) {
  };

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
    const newPVTree = {};
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

  protected updatePackages(packageNames: TemplatePackageVersion[]): void {
    if (packageNames && packageNames.length > 0) {
      let currentPackage = packageNames.pop();
      this.templateHttp.updatePackage(this.template, currentPackage.pkg).subscribe(
        () => {
          this.updatePackages(packageNames);
          this.alerts.success('The package ' + currentPackage.pkg + ' has been successfully updated.');
        },
        (error) => {
          this.updatePackages(packageNames);
          this.alerts.danger('The package update of ' + currentPackage.pkg + ' failed.');
        });
    }
  }

  private updateSelected(): void {
    let packageNames: TemplatePackageVersion[] = [];
    for (let pkg of this.packageVersions) {
      if (pkg.selected && !this.isPkgLatest(pkg)) {
        packageNames.push(pkg);
      }
    }
    this.updatePackages(packageNames);
    this.allSelected = false;
  }

  protected removePackage(pv: TemplatePackageVersion) {
    if (pv) {
      this.templateHttp.deletePackage(this.template, pv.pkg).subscribe(
        () => {
          this.alerts.success(`Package '${pv.pkg}' has been removed successfully.`);
        },
        (err) => {
          this.alerts.danger(`Error removing package '${pv.pkg}'!`);
        }
      )
    }
  }

  protected removePackages(packageNames: TemplatePackageVersion[]): void {
    if (packageNames && packageNames.length > 0) {
      let currentPackage = packageNames.pop();
      this.templateHttp.deletePackage(this.template, currentPackage.pkg).subscribe(
        () => {
          this.removePackages(packageNames);
          this.alerts.success(`Package '${currentPackage.pkg}' has been removed successfully.`);
        },
        (err) => {
          this.removePackages(packageNames);
          this.alerts.danger(`Error removing package '${currentPackage.pkg}'!`);
        }
      )
    }
  }


  private removeSelected(index = 0): void {
    let packageNames: TemplatePackageVersion[] = [];
    for (let pkg of this.packageVersions) {
      if (pkg.selected) {
        packageNames.push(pkg);
      }
    }
    this.removePackages(packageNames);
    this.allSelected = false;
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

  private isPkgLatest(pv: TemplatePackageVersion): boolean {
    return this.packageTree[pv.pkg] && this.packageTree[pv.pkg].newestVersion.version === pv.version;
  }

  private isPkgLatestByName(packageName: string): boolean {
    let index = this.packageVersions.findIndex((element) => element.pkg == packageName);
    return this.isPkgLatest(this.packageVersions[index]);
  }

  protected goToAddPackage(): void {
    this.newPackage = {pkg: '', version: ''};
  }

  protected addNewPackage(): void {
    if (this.newPackage && this.newPackage.pkg && this.newPackage.version
    ) {
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
    if (Validator.notEmpty(pkgName)
    ) {
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

  public exportFile(): void {
    const templateName = this.template.name;

    this.templateHttp.getSimplePackageVersions(templateName).subscribe(
      (simpePVs) => {
        const jsonBlob = new Blob([JSON.stringify(simpePVs, null, 2)], {type : 'application/json'});
        const filename =  [templateName, "template", this.datePipe.transform(new Date(), "yyyyMMdd")].join("-") + ".json";
        saveAs(jsonBlob, filename);
      },
      (err) => {
        this.alerts.danger(`Error exporting package versions for template '${templateName}'!`);
        console.error(err);
      }
    );
  }

  public importJSONFile(event: Event): void {
    if (!event || !event.target) {
      return;
    }
    const selectedFiles = event.target['files'];
    if (!selectedFiles || selectedFiles.length < 1) {
      return;
    }

    const reader = new FileReader();
    reader.readAsText(selectedFiles[0]);
    reader.onloadend = () => {
      let packageVersions: SimplePackageVersion[];
      try {
        packageVersions = JSON.parse(reader.result);
      } catch (err) {
        this.alerts.danger(`Error reading package versions from JSON file!`);
        console.error(err);
        return;
      }

      this.importing = true;
      this.templateHttp.replacePackageVersionsForTemplate(this.template.name, packageVersions)
        .finally(() => this.importing = false)
        .subscribe(
        (updatedTemplate) => this.alerts.success(`Successfully replaced packages in template '${updatedTemplate.name}'`),
        (err) => {
          if (!err.error) {
            this.alerts.danger(`Error updating package versions from JSON file!`);
            console.error(err);
            return;
          }
          this.alerts.danger(err.error);
        }
      );
    };
  }

}

