import { Component, AfterViewInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { AlertService } from "../util/alert/alert.service";
import { PackageHttpService, Package, PackageVersion } from "../util/http/package.http.service";
import { Validator } from "../util/validator.util";
import { ServiceHttpService, Service } from "../util/http/service.http.service";
import { Sorter } from "../util/sorters.util";

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
  moduleId: module.id,
  selector: 'package-detail',
  templateUrl: 'html/package.detail.comp.html'
})
export class PackageDetail implements AfterViewInit {

  private pkg: Package = {name: "", description: "", versions: []};

  protected templateRefs: Array<{template: string, version: string}> = [];

  private services: Array<Service>;
  protected addableServices: Array<Service> = [];
  private showAddService: boolean = false;
  private newService: Service;

  private back: any;

  private repos:Array<RepoVersionTree> = [];

  constructor(private packageHttp: PackageHttpService,
              private serviceHttp: ServiceHttpService,
              private route: ActivatedRoute, private router: Router,
              private alerts: AlertService) {
  };


  ngAfterViewInit(): void {
    this.route.params.subscribe((params) => {
      this.loadPackage(params['packageName']);
    });

    this.route.queryParams.subscribe((params) => {
      this.back = {ret: params['ret'], id: params['id']};
    });

  }

  private loadPackage(packageName: string) {
    if (Validator.notEmpty(packageName) && packageName != 'new') {
      this.packageHttp.getPackage(packageName).subscribe(
        (result) => {
          this.pkg = result;
          this.pkg.versions.sort(Sorter.versionComp);
          this.loadAssociatedServices();
          this.loadTemplateUsage();
          this.loadRepoProvision();
        },
        () => {
          this.alerts.danger("The package you are looking for doesn't not exists.");
          this.router.navigate(['package']);
        }
      );
    }
  }

  private loadAssociatedServices(): void {
    this.serviceHttp.getServices().subscribe(
      (result) => this.services = result.filter(service => this.serviceProvidedFilter(service)).sort(Sorter.service)
    )
  }

  private loadTemplateUsage():void {
    if(!this.pkg) {
      return;
    }

    this.packageHttp.getUsage(this.pkg).subscribe(
      (result) => {
        this.templateRefs = [];
        let keys = Object.keys(result);
        for (let key of keys) {
          this.templateRefs.push({template: key, version: result[key]});
        }
      });
  }

  private loadRepoProvision():void {
    if(!this.pkg) {
      return;
    }
    this.packageHttp.getVersions(this.pkg).subscribe(
      (result) => {

        let temp: {[repoName: string]: Array<PackageVersion>; } = {};
        for (let pv of result) {
          for(let repo of pv.repos) {
            if(!temp[repo]) {
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

  protected removeService(service: Service):void {
    if (Validator.idIsSet(service.id)) {
      let index = this.services.indexOf(service);
      service.packages.splice(service.packages.indexOf(this.pkg.name), 1);
      this.serviceHttp.save(service).subscribe(
        () => {
          this.services.splice(index, 1);
          this.alerts.success("You successfully removed the package from the service!")
        },
        () => this.alerts.danger("The package remove failed!")
      );
    }
  }

  protected saveNewService():void {
    if (this.newService) {
      this.newService.packages.push(this.pkg.name);
      this.serviceHttp.save(this.newService).subscribe(
        () => {
          this.services.push(this.newService);
          this.newService = null;
          this.showAddService = false;

          this.alerts.success("You successfully added the package to the service!")
        },
        () => this.alerts.danger("The connection of service and package failed!")
      );
    }
  }

  protected goToBack(): void {
    if (this.back) {
      if (this.back.ret == 'serviceDetail') {
        this.gotoService(this.back.id, true);
        return;
      }
      if (this.back.ret == 'repoDetail') {
        this.gotoRepo(this.back.id, true);
        return;
      }
    }
    this.router.navigate(['package']);
  }

  protected gotoTemplate(templateName: string) {
    this.router.navigate(['template', templateName]);
  }

  private gotoService(service: string, back?:boolean): void {
    if(back) {
      this.router.navigate(['service', service]);
    }else {
      this.router.navigate(['service', service], {queryParams: {ret: 'packageDetail', id: this.pkg.name}});
    }
  }

  private gotoRepo(repo: string, back?: boolean) {
    if(back) {
      this.router.navigate(['repo', repo]);
    }else {
      this.router.navigate(['repo', repo], {queryParams: {ret: 'packageDetail', id: this.pkg.name}});
    }
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


