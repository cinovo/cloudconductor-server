import { Location } from '@angular/common';
import { Component, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AlertService } from '../util/alert/alert.service';
import { PackageHttpService, Package, PackageVersion } from '../util/http/package.http.service';
import { Validator } from '../util/validator.util';
import { ServiceHttpService, Service } from '../util/http/service.http.service';
import { Sorter } from '../util/sorters.util';

interface RepoVersionTree {
  name: string;
  versions: PackageVersion[];
}

interface TemplateRef {
  template: string;
  version: string;
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'package-detail',
  templateUrl: './package.detail.comp.html'
})
export class PackageDetail implements AfterViewInit {

  public pkg: Package = {name: '', description: '', versions: []};

  public templateRefs: TemplateRef[] = [];

  public services: Service[];
  public addableServices: Service[] = [];
  public showAddService = false;
  public newService: Service;

  public repos: RepoVersionTree[] = [];

  constructor(private readonly packageHttp: PackageHttpService,
              private readonly serviceHttp: ServiceHttpService,
              private readonly route: ActivatedRoute,
              private readonly router: Router,
              private readonly alerts: AlertService,
              private readonly location: Location) { };


  ngAfterViewInit(): void {
    this.route.params.subscribe((params) => {
      this.loadPackage(params['packageName']);
    });
  }

  private loadPackage(packageName: string) {
    if (Validator.notEmpty(packageName) && packageName !== 'new') {
      this.packageHttp.getPackage(packageName).subscribe(
        (result) => {
          this.pkg = result;
          this.pkg.versions.sort(Sorter.versionComp);
          this.loadAssociatedServices();
          this.loadTemplateUsage();
          this.loadRepoProvision();
        },
        (_) => this.router.navigate(['/not-found', 'package', packageName])
      );
    }
  }

  private loadAssociatedServices(): void {
    this.serviceHttp.getServices().subscribe(
      (result) => this.services = result.filter(service => this.serviceProvidedFilter(service)).sort(Sorter.service)
    );
  }

  private loadTemplateUsage(): void {
    if (!this.pkg) {
      return;
    }

    this.packageHttp.getUsage(this.pkg).subscribe(
      (packageUsageMap) => {
        this.templateRefs = Object.entries(packageUsageMap)
          .map(([template, version]) => ({template, version} as TemplateRef))
          .sort((a, b) => Sorter.byField(a, b, 'template'));
      });
  }

  private loadRepoProvision(): void {
    if (!this.pkg) {
      return;
    }
    this.packageHttp.getVersions(this.pkg).subscribe(
      (result) => {

        let temp: {[repoName: string]: PackageVersion[]} = {};
        for (let pv of result) {
          for (let repo of pv.repos) {
            if (!temp[repo]) {
              temp[repo] = [];
            }
            temp[repo].push(pv);
          }
        }
        this.repos = [];
        for (let key in temp) {
          temp[key] = temp[key].sort(Sorter.packageVersion);
          this.repos.push({name: key, versions: temp[key]});
        }
        this.repos = this.repos.sort(Sorter.nameField);
      }
    );
  }

  public removeService(service: Service): void {
    if (Validator.idIsSet(service.id)) {
      const index = this.services.indexOf(service);
      service.packages.splice(service.packages.indexOf(this.pkg.name), 1);
      this.serviceHttp.save(service).subscribe(
        () => {
          this.services.splice(index, 1);
          this.alerts.success('You successfully removed the package from the service!')
        },
        () => this.alerts.danger('The package remove failed!')
      );
    }
  }

  public saveNewService(): void {
    if (this.newService) {
      this.newService.packages.push(this.pkg.name);
      this.serviceHttp.save(this.newService).subscribe(
        () => {
          this.services.push(this.newService);
          this.newService = null;
          this.showAddService = false;

          this.alerts.success('You successfully added the package to the service!')
        },
        () => this.alerts.danger('The connection of service and package failed!')
      );
    }
  }

  public goToBack(): void {
    this.location.back();
  }

  public gotoTemplate(templateName: string): Promise<boolean> {
    return this.router.navigate(['template', templateName]);
  }

  public gotoService(service: string): Promise<boolean> {
    return this.router.navigate(['service', service]);
  }

  public gotoRepo(repo: string): Promise<boolean> {
    return this.router.navigate(['repo', repo]);
  }

  public goToAddService(): void {
    this.serviceHttp.getServices().subscribe(
      (result) => this.addableServices = result.filter((service) => this.serviceNotProvidedFilter(service)).sort(Sorter.service)
    );
    this.showAddService = true;
    this.newService = null;
  }

  private serviceProvidedFilter(service: Service): boolean {
    return service.packages.includes(this.pkg.name);
  }

  private serviceNotProvidedFilter(service: Service): boolean {
    return !service.packages.includes(this.pkg.name);
  }

}


