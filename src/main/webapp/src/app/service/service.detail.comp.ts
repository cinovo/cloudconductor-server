import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
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
  private _allPackages: Package[] = [];

  public mode: Mode = 'edit';
  public serviceForm: FormGroup;

  constructor(private serviceHttp: ServiceHttpService,
              private packageHttp: PackageHttpService,
              private route: ActivatedRoute,
              private router: Router,
              private alerts: AlertService,
              private fb: FormBuilder,
              private location: Location) {
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
        this.mode = 'edit';
        this.loadData(serviceName);
      }
    });

    this.packageHttp.getPackages().subscribe(
      (result) => {
        this.allPackages = result;
      }, (err) => {
        this.alerts.danger('Error loading packages!');
        console.error(err);
      }
    );
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

      this.reloadServiceUsage(serviceName);
    }
  }

  private reloadServiceUsage(serviceName: string): void {
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

  public get allPackages() {
    return this._allPackages;
  }

  public set allPackages(value: Package[]) {
    this._allPackages = value.filter((pkg) => this.filterUsedPackages(pkg))
                              .sort(Sorter.packages);
  }

  public save(formValue): void {
    this.service.name = formValue.name.trim();
    this.service.initScript = formValue.initScript;
    this.service.description = formValue.description;

    const check = (this.mode === 'new') ? this.serviceHttp.existsService(this.service.name) : Observable.of(false);
    check.flatMap((exists) => {
      if (exists) {
        return Observable.throw(`Service named '${this.service.name}' already exists!`);
      }

      return this.serviceHttp.save(this.service)
    }).subscribe(
      (result) => {
        this.alerts.success(`Successfully saved service '${this.service.name}'!`);

        // use router to go into edit mode
        this.router.navigate(['/service', this.service.name]);
      }, (err) => this.alerts.danger(`Error saving service: ${err}`)
    );
  }

  protected deletePackage(pkg: string) {
    if (Validator.idIsSet(this.service.id)) {
      this.serviceHttp.getService(this.service.name).subscribe(
        (result) => {
          result.packages.splice(result.packages.indexOf(pkg), 1);
          this.serviceHttp.save(result).subscribe(
            () => {
              this.service.packages.splice(this.service.packages.indexOf(pkg), 1);
              this.alerts.success(`Successfully removed package '${pkg}' from service '${this.service.name}'!`);

              // deleting a package may change service usage
              this.reloadServiceUsage(this.service.name);
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
    if (!Validator.notEmpty(this.serviceForm.get('name').value)) {
      this.serviceForm.patchValue({name : this.newPackage});
    }
    if (!Validator.notEmpty(this.serviceForm.get('description').value)) {
      this.serviceForm.patchValue({description : this.newPackage});
    }
    if (!Validator.notEmpty(this.serviceForm.get('initScript').value)) {
      this.serviceForm.patchValue({initScript : this.newPackage});
    }
    this.newPackage = null;
    this.showAddPackage = false;

    // usage may have changed now
    if (this.service.name) {
      this.reloadServiceUsage(this.service.name);
    }
  }

  protected goToAddPackage() {
    this.newPackage = null;
    this.showAddPackage = true;
  }

  public goToBack(): void {
    this.router.navigate(['/service']);
  }

  private gotoPackage(pkgName: string) {
    this.router.navigate(['package', pkgName]);
  }

  protected gotoTemplate(templateName: string) {
    this.router.navigate(['template', templateName]);
  }

  private filterUsedPackages(pkg: Package): boolean {
    return !this.service.packages.includes(pkg.name);
  }

}
