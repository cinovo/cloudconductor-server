import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { Service, ServiceHttpService } from '../util/http/service.http.service';
import { AlertService } from '../util/alert/alert.service';
import { PackageHttpService, Package } from '../util/http/package.http.service';
import { Sorter } from '../util/sorters.util';
import { forbiddenNameValidator, Validator } from '../util/validator.util';

type Mode = 'new' | 'edit';

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
export class ServiceDetail implements OnInit {

  public service: Service = {id: -1, name: '', description: '', initScript: '', packages: []};
  public templateRefs: Array<{template: string, pkg: string}> = [];
  public showAddPackage = false;
  private newPackage: string;
  protected allPackages: Array<Package> = [];
  private back: any;

  public mode: Mode = 'edit';
  public serviceForm: FormGroup;

  constructor(private serviceHttp: ServiceHttpService,
              private packageHttp: PackageHttpService,
              private route: ActivatedRoute,
              private router: Router,
              private alerts: AlertService,
              private fb: FormBuilder) {
    this.serviceForm = fb.group({
      name: ['', [Validators.required, forbiddenNameValidator('new')]],
      initScript: ['', Validators.required],
      description: ['']
    });
  };

  public ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const serviceName = params['serviceName'];

      if (serviceName === 'new') {
        this.mode = 'new';
      } else {
        this.loadData(serviceName);
      }
    });
    this.route.queryParams.subscribe((params) => {
      this.back = {ret: params['ret'], id: params['id']};
    });
  }

  private loadData(serviceName: string) {
    if (Validator.notEmpty(serviceName) && serviceName !== 'new') {
      this.serviceHttp.getService(serviceName).subscribe(
        (result) => {
          this.service = result;
          this.service.packages.sort();

          this.serviceForm.setValue({name: this.service.name,
            initScript: this.service.initScript,
            description: this.service.description});
        },
        (err) => this.router.navigate(['/not-found', 'service', serviceName])
      );

      this.serviceHttp.getServiceUsage(serviceName).subscribe(
        (result) => {
          this.templateRefs = [];
          let keys = Object.keys(result);
          for (let key of keys) {
            this.templateRefs.push({template: key, pkg: result[key]});
          }
          this.templateRefs.sort((a, b) => Sorter.byField(a, b, 'template'));
        });
    }
  }

  public save(formValue): void {
    this.service.name = formValue.name;
    this.service.initScript = formValue.initScript;
    this.service.description = formValue.description;

    const check = (this.mode === 'new') ? this.serviceHttp.existsService(this.service.name) : Observable.of(false);
    check.flatMap((exists) => {
      if (exists) {
        return Observable.throw(`Service named '${this.service.name}' already exists!`);
      }

      return this.serviceHttp.save(this.service)
    }).subscribe(
      (result) => this.alerts.success(`Successfully saved service '${this.service.name}'!`),
      (err) => this.alerts.danger(`Error saving service: ${err}`)
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
              this.alerts.success('You successfully removed the package from the service!')
            },
            () => this.alerts.danger('The package remove failed!')
          );
        },
        () => {
          this.alerts.danger('The package remove failed!');
        }
      );
    } else {
      this.service.packages.splice(this.service.packages.indexOf(pkg), 1);
    }
  }

  protected saveNewPackage() {
    if (!Validator.notEmpty(this.newPackage)) {
      this.alerts.warning('Please select a package before saving!');
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
          () => this.alerts.danger('The package add failed!')
        );
      },
      () => this.alerts.danger('The package add failed!')
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
    this.packageHttp.getPackages()
      .subscribe((result) => this.allPackages = result.filter((pkg) => this.filterUsedPackages(pkg)).sort(Sorter.packages));
    this.showAddPackage = true;

  }

  public goToBack(): void {
    if (this.back) {
      if (this.back.ret === 'packageDetail') {
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
