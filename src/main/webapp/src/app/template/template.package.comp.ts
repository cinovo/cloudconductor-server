import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { DatePipe } from "@angular/common";

import { Observable, Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { saveAs } from "file-saver";

import { PackageVersionMultiMap, Template, TemplateHttpService } from '../util/http/template.http.service';
import { SimplePackageVersion } from '../util/http/package.http.service';
import { Sorter } from '../util/sorters.util';
import { AlertService } from '../util/alert/alert.service';
import { Validator } from '../util/validator.util';

export interface TemplatePackageVersion {
  pkg: string,
  version: string,
  selected: boolean
}

type NewPackageVersion = {
  pkg: string;
  version: string;
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch, mweise
 */
@Component({
    selector: 'template-packages',
    templateUrl: './template.package.comp.html',
    standalone: false
})
export class TemplatePackages implements OnInit, OnDestroy {

  @Input() obsTemplate: Observable<Template>;

  private template: Template = {name: '', description: ''};
  public packageVersions: TemplatePackageVersion[] = [];

  public pvAvailable: PackageVersionMultiMap;
  public pvInRange: PackageVersionMultiMap;

  public newPackage: NewPackageVersion = null;

  public importing = false;

  private _allSelected = false;
  private templateSub: Subscription;

  constructor(private readonly templateHttp: TemplateHttpService,
              private readonly alerts: AlertService,
              private readonly datePipe: DatePipe) {
  }

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
    this.loadUpdates(template.name);
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

  private loadUpdates(templateName: string): void {
    if (!templateName || templateName.length < 1){
      return
    }

    this.templateHttp.getUpdates(templateName).subscribe(
      (updates) => {
        this.pvAvailable = updates.available;
        this.pvInRange = updates.inRange;
      },
      (err) => console.error(err)
    );
  }

  public updatePackage(pv: TemplatePackageVersion): void {
    if (!pv) {
      return;
    }
    this.templateHttp.updatePackage(this.template, pv.pkg).subscribe(
      () => this.alerts.success('The package ' + pv.pkg + ' has been updated successfully.'),
      (err) => {
        this.alerts.danger('The package update of ' + pv.pkg + ' failed.');
        console.error(err);
      }
    );
  }

  protected updatePackages(packageNames: TemplatePackageVersion[]): void {
    if (packageNames && packageNames.length > 0) {
      const currentPackage = packageNames.pop();
      this.templateHttp.updatePackage(this.template, currentPackage.pkg).subscribe(
        () => {
          this.updatePackages(packageNames);
          this.alerts.success('The package ' + currentPackage.pkg + ' has been updated successfully.');
        },
        (err) => {
          this.updatePackages(packageNames);
          this.alerts.danger('The package update of ' + currentPackage.pkg + ' failed.');
          console.error(err);
        });
    }
  }

  public updateSelected(): void {
    const selected = this.packageVersions.filter(pv => pv.selected && !this.isPkgLatest(pv));
    this.updatePackages(selected);
    this.allSelected = false;
  }

  public removePackage(pv: TemplatePackageVersion) {
    if (pv) {
      this.templateHttp.deletePackage(this.template, pv.pkg).subscribe(
        () => {
          this.alerts.success(`Package '${pv.pkg}' has been removed successfully.`);
        },
        (err) => {
          console.error(err);
          this.alerts.danger(`Error removing package '${pv.pkg}'!`);
        }
      )
    }
  }

  protected removePackages(packageNames: TemplatePackageVersion[]): void {
    if (packageNames && packageNames.length > 0) {
      const currentPackage = packageNames.pop();
      this.templateHttp.deletePackage(this.template, currentPackage.pkg).subscribe(
        () => {
          this.removePackages(packageNames);
          this.alerts.success(`Package '${currentPackage.pkg}' has been removed successfully.`);
        },
        (err) => {
          this.removePackages(packageNames);
          this.alerts.danger(`Error removing package '${currentPackage.pkg}'!`);
          console.error(err);
        }
      )
    }
  }

  public removeSelected(index = 0): void {
    const selected = this.packageVersions.filter(pv => pv.selected);
    this.removePackages(selected);
    this.allSelected = false;
  }

  get allSelected(): boolean {
    return this._allSelected;
  }

  set allSelected(value: boolean) {
    this._allSelected = value;
    for (const pv of this.packageVersions) {
      pv.selected = this._allSelected;
    }
  }

  public selectPackage(pv: TemplatePackageVersion, event: any) {
    const index = this.packageVersions.indexOf(pv);
    if (index > -1) {
      this.packageVersions[index].selected = event.target.checked;
    }
  }

  public isPkgSelected(): boolean {
    return this.packageVersions.some(pv => pv.selected);
  }

  public getLatestPkg(pv: TemplatePackageVersion): string {
    return (this.pvAvailable && this.pvAvailable[pv.pkg]) ? this.pvAvailable[pv.pkg].slice(-1).pop() : null;
  }

  public isPkgLatest(pv: TemplatePackageVersion): boolean {
    return pv.version === this.getLatestPkg(pv);
  }

  public getLatestPkgInRange(pv: TemplatePackageVersion): string {
    return (this.pvInRange && this.pvInRange[pv.pkg]) ? this.pvInRange[pv.pkg].slice(-1).pop() : null;
  }

  public isPkgLatestInRange(pv: TemplatePackageVersion): boolean {
    return pv.version === this.getLatestPkgInRange(pv);
  }

  public goToAddPackage(): void {
    this.newPackage = {pkg: '', version: ''};
  }

  public updatePackageVersion(pkg: string, version: string) {
    if (!pkg || !version) {
      return;
    }
    this.updatePackageForTemplate(this.template.name, { pkg, version });
  }

  public addNewPackage(): void {
    if (!this.newPackage || !this.newPackage.pkg || !this.newPackage.version) {
      return;
    }

    this.updatePackageForTemplate(this.template.name, {...this.newPackage});
  }

  private updatePackageForTemplate(templateName: string, updatedPackage: NewPackageVersion) {
    this.templateHttp.getTemplate(templateName).subscribe(
      (result) => {
        result.versions[updatedPackage.pkg] = updatedPackage.version;
        this.templateHttp.save(result).subscribe(
          () => {
            this.alerts.success(`Successfully updated package '${updatedPackage.pkg} to version '${updatedPackage.version}'`);
            this.newPackage = null;
          },
          () => {
            this.alerts.danger(`Failed to update package '${updatedPackage.pkg}' to version '${updatedPackage.version}'!`);
            this.cancelAddPackage();
          }
        )
      },
      () => {
        this.alerts.danger(`Failed to update package '${updatedPackage.pkg}' to version '${updatedPackage.version}'!`);
        this.cancelAddPackage();
      }
    );
  }
  public getAvailableVersionStrings(pkgName: string): string[] {
    if (Validator.notEmpty(pkgName) && this.pvAvailable && this.pvAvailable[pkgName]) {
        return this.pvAvailable[pkgName].slice().sort(Sorter.versionComp);
    }
    return [];
  }

  public onPackageChange(): void {
    const versions = this.getAvailableVersionStrings(this.newPackage.pkg);
    if (versions.length > 0) {
      this.newPackage.version = versions.slice(-1).pop();
    } else {
      this.newPackage.version = '';
    }
  }

  public cancelAddPackage(): void {
    this.newPackage = null;
  }

  public getInstallablePackages(): string[] {
    return Object.keys(this.pvAvailable).filter(pkg => !this.templateContainsPackage(pkg))
  }

  private templateContainsPackage(pkgName: string): boolean {
    return Object.keys(this.template.versions).includes(pkgName);
  }

  public exportFile(): void {
    const templateName = this.template.name;

    this.templateHttp.getSimplePackageVersions(templateName).subscribe(
      (simplePVs) => {
        const jsonBlob = new Blob([JSON.stringify(simplePVs, null, 2)], {type : 'application/json'});
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
        packageVersions = JSON.parse("" + reader.result);
      } catch (err) {
        this.alerts.danger(`Error reading package versions from JSON file!`);
        console.error(err);
        return;
      }

      this.importing = true;
      this.templateHttp.replacePackageVersionsForTemplate(this.template.name, packageVersions).pipe(
        finalize(() => this.importing = false))
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

