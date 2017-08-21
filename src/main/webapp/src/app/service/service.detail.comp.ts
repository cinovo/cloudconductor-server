import { Component, AfterViewInit } from "@angular/core";
import { Service, ServiceHttpService } from "../util/http/service.http.service";
import { ActivatedRoute, Router } from "@angular/router";
import { AlertService } from "../util/alert/alert.service";
import { PackageHttpService, Package } from "../util/http/package.http.service";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'service-detail',
  templateUrl: './service.detail.comp.html'
})
export class ServiceDetail implements AfterViewInit {

  private service: Service = {id: -1, name: "", description: "", initScript: "", packages: []};
  protected templateRefs: Array<{template: string, pkg: string}> = [];
  private showAddPackage: boolean = false;
  private newPackage: string;
  protected allPackages: Array<Package> = [];
  private back: any;


  constructor(private serviceHttp: ServiceHttpService, private packageHttp: PackageHttpService,
              private route: ActivatedRoute, private router: Router,
              private alerts: AlertService) {
  };


  ngAfterViewInit(): void {
    this.route.params.subscribe((params) => {
      this.loadData(params['serviceName']);
    });
    this.route.queryParams.subscribe((params) => {
      this.back = {ret: params['ret'], id: params['id']};
    });
  }

  private loadData(serviceName: string) {
    if (Validator.notEmpty(serviceName) && serviceName != 'new') {
      this.serviceHttp.getService(serviceName).subscribe(
        (result) => {
          this.service = result;
          this.service.packages.sort();
        },
        () => {
          this.alerts.danger("The service you are looking for doesn't not exists.");
          this.router.navigate(['service']);
        }
      );

      this.serviceHttp.getServiceUsage(serviceName).subscribe(
        (result) => {
          this.templateRefs = [];
          let keys = Object.keys(result);
          for (let key of keys) {
            this.templateRefs.push({template: key, pkg: result[key]});
          }
        });
    }
  }

  private save(): void {
    if (this.fieldValidation()) {
      return;
    }
    this.serviceHttp.save(this.service).subscribe(
      (result) => this.alerts.success("You successfully saved the service!"),
      (error) => this.alerts.danger("The save Failed!")
    );
  }

  protected deletePackage(pkg) {
    if (Validator.idIsSet(this.service.id)) {
      this.serviceHttp.getService(this.service.name).subscribe(
        (result) => {
          result.packages.splice(result.packages.indexOf(pkg), 1);
          this.serviceHttp.save(result).subscribe(
            () => {
              this.service.packages.splice(this.service.packages.indexOf(pkg), 1);
              this.alerts.success("You successfully removed the package from the service!")
            },
            () => this.alerts.danger("The package remove failed!")
          );
        },
        () => {
          this.alerts.danger("The package remove failed!");
        }
      );
    } else {
      this.service.packages.splice(this.service.packages.indexOf(pkg), 1);
    }
  }

  protected saveNewPackage() {
    if (!Validator.notEmpty(this.newPackage)) {
      this.alerts.warning("Please select a package before saving!");
      return;
    }
    if (!Validator.idIsSet(this.service.id)) {
      this.localAddPackage();
      return;
    }
    this.serviceHttp.getService(this.service.name).subscribe(
      (service) => {
        service.packages.push(this.newPackage);
        this.serviceHttp.save(service).subscribe(
          () => this.localAddPackage(),
          () => this.alerts.danger("The package add failed!")
        );
      },
      () => this.alerts.danger("The package add failed!")
    );
  }

  private localAddPackage() {
    this.service.packages.push(this.newPackage);
    this.service.packages.sort();
    if (!Validator.notEmpty(this.service.name)) {
      this.service.name = this.newPackage;
    }
    if (!Validator.notEmpty(this.service.description)) {
      this.service.description = this.newPackage;
    }
    if (Validator.notEmpty(this.service.initScript)) {
      this.service.initScript = this.newPackage;
    }
    this.newPackage = null;
    this.showAddPackage = false;
  }

  protected goToAddPackage() {
    this.newPackage = null;
    this.packageHttp.getPackages().subscribe((result) => this.allPackages = result.filter((pkg) => this.filterUsedPackages(pkg)).sort(Sorter.packages));
    this.showAddPackage = true;

  }

  private fieldValidation() {
    let error = false;
    if (!this.isNameValid()) {
      this.alerts.danger("Please insert a valid service name.");
      error = true;
    }
    if (!this.isScriptValid()) {
      this.alerts.danger("Please insert a valid start-script.");
      error = true;
    }
    return error;
  }

  private isNameValid() {
    return Validator.notEmpty(this.service.name);
  }

  private isScriptValid() {
    return Validator.notEmpty(this.service.initScript);
  }

  protected goToBack(): void {
    if (this.back) {
      if (this.back.ret == 'packageDetail') {
        this.gotoPackage(this.back.id, true);
        return;
      }
    }
    this.router.navigate(['service']);
  }

  private gotoPackage(pkgName: string, back?: boolean) {
    if (back) {
      this.router.navigate(['package', pkgName]);
    } else {
      this.router.navigate(['package', pkgName], {queryParams: {ret: 'serviceDetail', id: this.service.name}});
    }
  }

  protected gotoTemplate(templateName: string) {
    this.router.navigate(['template', templateName]);
  }

  private filterUsedPackages(pkg: Package): boolean {
    return !this.service.packages.includes(pkg.name);
  }
}
