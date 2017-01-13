import { Component, AfterViewInit } from "@angular/core";
import { Service, ServiceHttpService } from "../services/http/service.http.service";
import { ActivatedRoute, Router } from "@angular/router";
import { AlertService } from "../services/alert/alert.service";
import { PackageHttpService, Package } from "../services/http/package.http.service";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";
@Component({
  moduleId: module.id,
  selector: 'service-detail',
  templateUrl: 'html/service.detail.comp.html'
})
export class ServiceDetail implements AfterViewInit {

  private service: Service = {id: -1, name: "", description: "", initScript: "", packages: []};
  private templateRefs: Array<{template: string, pkg: string}> = [];
  private showAddPackage: boolean = false;
  private newPackage: string;
  private allPackages: Array<Package> = [];


  constructor(private serviceHttp: ServiceHttpService, private packageHttp: PackageHttpService,
              private route: ActivatedRoute, private router: Router,
              private alerts: AlertService) {
  };


  ngAfterViewInit(): void {
    this.route.params.subscribe((params) => {
      this.loadData(params['serviceName']);
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
          this.router.navigate(['services']);
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

  private deletePackage(pkg) {
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

  private saveNewPackage() {
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
          (succ) => {
            this.localAddPackage();
          },
          (error) => this.alerts.danger("The package add failed!")
        );
      },
      (error) => {
        this.alerts.danger("The package add failed!");
      }
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

  private goToAddPackage() {
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

  private gotoPackage(pkgName: string) {
    this.router.navigate(['package'], pkgName);
  }

  private gotoTemplate(templateName: string) {
    this.router.navigate(['template', templateName]);
  }

  private filterUsedPackages(pkg: Package): boolean {
    return !this.service.packages.includes(pkg.name);
  }
}
