import { Location } from '@angular/common';
import { Component, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AlertService } from '../util/alert/alert.service';
import { PackageHttpService, Package, PackageVersion } from '../util/http/package.http.service';
import { Validator } from '../util/validator.util';
import { ServiceHttpService, Service } from '../util/http/service.http.service';
import { Sorter } from '../util/sorters.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */

interface RepoVersionTree {
  name: string;
  versions: Array<PackageVersion>;
}

@Component({
  selector: 'package-detail',
  templateUrl: './package.detail.comp.html'
})
export class PackageDetail implements AfterViewInit {

  public pkg: Package = {name: '', description: '', versions: []};

  public templateRefs: Array<{template: string, version: string}> = [];

  public services: Array<Service>;
  protected addableServices: Array<Service> = [];
  public showAddService = false;
  private newService: Service;

  public repos: Array<RepoVersionTree> = [];

  constructor(private packageHttp: PackageHttpService,
              private serviceHttp: ServiceHttpService,
              private route: ActivatedRoute,
              private router: Router,
              private alerts: AlertService,
              private location: Location) { };


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
        (err) => this.router.navigate(['/not-found', 'package', packageName])
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
      (result) => {
        this.templateRefs = [];
        let keys = Object.keys(result);
        for (let key of keys) {
          this.templateRefs.push({template: key, version: result[key]});
        }
        this.templateRefs.sort((a, b) => Sorter.byField(a, b, 'template'));
      });
  }

  private loadRepoProvision(): void {
    if (!this.pkg) {
      return;
    }
    this.packageHttp.getVersions(this.pkg).subscribe(
      (result) => {

        let temp: {[repoName: string]: Array<PackageVersion>; } = {};
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
    )
  }

  protected removeService(service: Service): void {
    if (Validator.idIsSet(service.id)) {
      let index = this.services.indexOf(service);
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

  protected saveNewService(): void {
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

  protected gotoTemplate(templateName: string) {
    this.router.navigate(['template', templateName]);
  }

  private gotoService(service: string): void {
    this.router.navigate(['service', service]);
  }

  private gotoRepo(repo: string) {
    this.router.navigate(['repo', repo]);
  }

  protected goToAddService(): void {
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


