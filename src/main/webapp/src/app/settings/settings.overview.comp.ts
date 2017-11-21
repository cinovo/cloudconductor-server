import { Component, AfterViewInit, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { SettingHttpService, Settings } from '../util/http/setting.http.service';
import { AlertService } from '../util/alert/alert.service';
import { Package, PackageHttpService } from '../util/http/package.http.service';
import { Sorter } from '../util/sorters.util';
import { Validator, gtValidator } from '../util/validator.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'settings-overview',
  templateUrl: './settings.overview.comp.html'
})
export class SettingsOverview implements OnInit {

  public readonly timeUnits = [
    { value: 'SECONDS', label: 'Seconds' },
    { value: 'MINUTES', label: 'Minutes' },
    { value: 'HOURS', label: 'Hours' },
    { value: 'DAYS', label: 'Days' }
  ];

  public settingsForm: FormGroup;

  protected allPackages: Array<Package> = [];
  public newPackage: string;
  public showAddPackage = false;

  constructor(private settingHttp: SettingHttpService,
              private packageHttp: PackageHttpService,
              private alerts: AlertService,
              private fb: FormBuilder) {
    this.settingsForm = fb.group({
      name: ['Default', [Validators.required]],
      description: [''],
      allowautoupdate: [false],
      hostCleanUpTimer: [60, [Validators.required, gtValidator(0)]],
      hostCleanUpTimerUnit: ['SECONDS', [Validators.required]],
      indexScanTimer: [60, [Validators.required, gtValidator(0)]],
      indexScanTimerUnit: ['SECONDS', [Validators.required]],
      pageRefreshTimer: [60, [Validators.required, gtValidator(0)]],
      pageRefreshTimerUnit: ['SECONDS', [Validators.required]],
      disallowUninstall: [[]],
      newPackage: ['']
    });
  };

  ngOnInit(): void {
    this.loadSettings();
  }

  private loadSettings(): void {
    this.settingHttp.settings.subscribe(
      (loadedSettings) => {
        this.settingsForm.patchValue(loadedSettings);
      },
      (err) => {
        this.alerts.danger('Error loading settings from server!');
        console.error(err);
      }
    );
  }

  public saveSettings(settings: Settings): void {
    const updatedSettings = {
      name: settings.name,
      description: settings.description,
      allowautoupdate: settings.allowautoupdate,
      hostCleanUpTimer: settings.hostCleanUpTimer,
      hostCleanUpTimerUnit: settings.hostCleanUpTimerUnit,
      indexScanTimer: settings.indexScanTimer,
      indexScanTimerUnit: settings.indexScanTimerUnit,
      pageRefreshTimer: settings.pageRefreshTimer,
      pageRefreshTimerUnit: settings.pageRefreshTimerUnit,
      disallowUninstall: settings.disallowUninstall
    }

    this.settingHttp.save(updatedSettings).subscribe(
      (result) => this.alerts.success('Settings have been successfully saved.'),
      (error) => {
        this.alerts.danger('Error saving settings!');
        console.log(error);
      }
    )
  }

  protected removePackage(pkg: string): void {
    const settings = this.settingsForm.value;
    let index = settings.disallowUninstall.indexOf(pkg);
    settings.disallowUninstall.splice(index, 1);
    settings.disallowUninstall.sort();
  }

  protected saveNewPackage(): void {
    const settings = this.settingsForm.value;
    if (Validator.notEmpty(settings.newPackage) && settings.disallowUninstall.indexOf(settings.newPackage) < 0) {
      settings.disallowUninstall.push(settings.newPackage);
      settings.disallowUninstall.sort();
      settings.newPackage = null;
      this.showAddPackage = false;
    }
  }

  protected goToAddPackage(): void {
    this.settingsForm.value.newPackage = null;
    this.packageHttp.getPackages().subscribe((result) => {
      this.allPackages = result.filter((pkg) => this.filterUsedPackages(pkg))
                                .sort(Sorter.packages);
    });
    this.showAddPackage = true;
  }

  private filterUsedPackages(pkg: Package): boolean {
    return !this.settingsForm.value.disallowUninstall.includes(pkg.name);
  }
}
