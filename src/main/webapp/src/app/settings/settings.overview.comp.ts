import { Component, AfterViewInit } from "@angular/core";
import { SettingHttpService, Settings } from "../services/http/setting.http.service";
import { AlertService } from "../services/alert/alert.service";
import { Package, PackageHttpService } from "../services/http/package.http.service";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  moduleId: module.id,
  selector: 'settings-overview',
  templateUrl: 'html/settings.overview.comp.html'
})

export class SettingsOverview implements AfterViewInit {

  private settings: Settings = {};

  protected allPackages: Array<Package> = [];
  private newPackage: string;
  private showAddPackage: boolean = false;

  constructor(private settingHttp: SettingHttpService, private packageHttp:PackageHttpService, private alerts: AlertService) {
  };

  ngAfterViewInit(): void {
    this.loadSettings();
  }

  private loadSettings():void {
    this.settingHttp.settings.subscribe(
      (result) => {
        this.settings = result;
      }
    );
  }


  protected saveSettings():void {
    this.settingHttp.save(this.settings).subscribe(
      (result) => this.alerts.success("The settings have been successfully saved."),
      (error) =>  this.alerts.danger("The settings has failed to save.")
    )
  }

  protected removePackage(pkg:string):void {
    let index = this.settings.disallowUninstall.indexOf(pkg);
    this.settings.disallowUninstall.splice(index, 1);
    this.settings.disallowUninstall.sort();
  }

  protected saveNewPackage():void {
    if(Validator.notEmpty(this.newPackage) && this.settings.disallowUninstall.indexOf(this.newPackage) < 0) {
      this.settings.disallowUninstall.push(this.newPackage);
      this.settings.disallowUninstall.sort();
      this.newPackage = null;
      this.showAddPackage = false;
    }
  }

  protected goToAddPackage():void {
    this.newPackage = null;
    this.packageHttp.getPackages().subscribe((result) => this.allPackages = result.filter((pkg) => this.filterUsedPackages(pkg)).sort(Sorter.packages));
    this.showAddPackage = true;
  }

  private filterUsedPackages(pkg: Package): boolean {
    return !this.settings.disallowUninstall.includes(pkg.name);
  }
}
