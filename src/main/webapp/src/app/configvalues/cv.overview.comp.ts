import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ConfigValue, ConfigValueHttpService } from '../util/http/configValue.http.service';
import { Sorter } from '../util/sorters.util';
import { Validator } from '../util/validator.util';
import { AlertService } from '../util/alert/alert.service';

interface ConfigValueTreeNode {
  name: string;
  kvs: ConfigValue[];
  icon: string;
}

interface ServiceConfigMap {
  [serviceName: string]: ConfigValue[];
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cv-overview',
  templateUrl: './cv.overview.comp.html'
})
export class ConfigValueOverview implements OnInit, OnDestroy {

  private _searchQuery: string = null;
  private routeSub: Subscription;
  private allCVs: ConfigValue[] = [];

  public template: string;
  public kvLoaded = false;
  public edit: boolean[] = [];
  public tree: ConfigValueTreeNode[] = [];

  constructor(private readonly configHttp: ConfigValueHttpService,
              private readonly route: ActivatedRoute,
              private readonly router: Router,
              private readonly alerts: AlertService) {
  };

  ngOnInit(): void {
    this.routeSub = this.route.paramMap.subscribe((paramMap) => {
      this.template = paramMap.get('template');
      this.loadData();
    });
  }

  ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.tree = this.generateTree();
  }

  private filterData(configValue: ConfigValue): boolean {
    if (configValue.service === "VARIABLES") {
      return false;
    }
    if (!Validator.notEmpty(this._searchQuery)) {
      return true;
    }
    return Object.values(configValue).some(value => typeof value === "string" && value.includes(this._searchQuery.trim()))
  }

  public deleteCurrentTemplate(): void {
    this.configHttp.deleteForTemplate(this.template)
      .subscribe(
        () => {
          this.alerts.success(`All config values for template '${this.template}' were deleted successfully!`);
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['config']);
        },
        (err) => {
          this.alerts.danger(`Error deleting config values for template '${this.template}'!`);
          console.error(err);
        }
      );
  }

  public deleteService(serviceName: string): void {
    if (!serviceName || serviceName.length < 1) {
      serviceName = "GLOBAL";
    }
    this.configHttp.deleteForService(this.template, serviceName)
      .subscribe(
        () => {
          this.alerts.success(`Successfully deleted all configuration values for service '${serviceName}'`);
          this.loadData();
        },
        (err) => {
          this.alerts.danger(`Error deleting configuration values for service '${serviceName}'!`);
          console.error(err);
        }
      );
  }

  public triggerEdit(kv: ConfigValue): void {
    if (!this.edit[kv.service + kv.key]) {
      this.edit[kv.service + kv.key] = false;
    }
    this.edit[kv.service + kv.key] = true;
  }

  public triggerEditDone(kv: ConfigValue): void {
    if (this.edit[kv.service + kv.key]) {
      this.edit[kv.service + kv.key] = false;
    }
  }

  public doDelete(kv: ConfigValue) {
    this.configHttp.deleteValue(kv).subscribe(() => this.loadData(),
      (err) => {
        this.alerts.danger(`Error deleting config pair '${kv.key}'-'${kv.value}'!`);
        console.error(err);
      }
    );
  }

  private generateTree(): ConfigValueTreeNode[] {
    const tmpServiceConfigMap: ServiceConfigMap = this.allCVs
      .filter(this.filterData.bind(this))
      .reduce((serviceMap , currentCV) => {
        const serviceName = currentCV.service || '';
        if (!serviceMap[serviceName]) {
          serviceMap[serviceName] = [];
        }
        serviceMap[serviceName].push(currentCV);
        return serviceMap
      }, {} as ServiceConfigMap);

    return Object.entries(tmpServiceConfigMap)
      .map(([name, cvs]) => ({name, kvs: cvs.sort(Sorter.configValue), icon: ConfigValueOverview.getIcon(name)}) as ConfigValueTreeNode)
      .sort(Sorter.nameField);
  }

  private static getIcon(name: string): string {
    const globalIcon = 'fa-institution';
    const serviceIcon = 'fa-flask';
    return name.length === 0 ? globalIcon : serviceIcon;
  }

  private loadData(): void {
    this.configHttp.getValues(this.template).pipe(
      finalize(() => this.kvLoaded = true))
      .subscribe(
        (result) => {
          this.allCVs = result;
          this.tree = this.generateTree()
        }, (err) => {
          this.alerts.danger(`Error loading config values for template '${this.template}'!`);
          console.error(err);
        });
  }

  public save(kv: ConfigValue, newVal: any): void {
    const oldVal = kv.value;
    kv.value = newVal;
    this.configHttp.save(kv).subscribe(() => this.alerts.success(`Modified value for key '${kv.key}'`),
      (err) => {
        kv.value = oldVal;
        this.alerts.danger(`Failed to modify value for key '${kv.key}'`);
        console.error(err);
      });
  }

}
