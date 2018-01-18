import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { AlertService } from '../util/alert/alert.service';
import { forbiddenNameValidator, Validator } from '../util/validator.util';
import { Repo, RepoHttpService } from '../util/http/repo.http.service';
import { RepoMirrorHttpService } from '../util/http/repomirror.http.service';
import { PackageHttpService, PackageVersion } from '../util/http/package.http.service';
import { Sorter } from '../util/sorters.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'repo-edit',
  templateUrl: './repo.edit.comp.html'
})
export class RepoEdit implements OnInit {

  public mode = 'edit';
  public repo: Repo = {name: '', mirrors: [], primaryMirror: 0};

  public packages: Array<PackageVersion> = [];

  public repoForm: FormGroup;

  constructor(private repoHttp: RepoHttpService,
              private mirrorHttp: RepoMirrorHttpService,
              private packageHttp: PackageHttpService,
              private route: ActivatedRoute,
              private alerts: AlertService,
              private router: Router,
              private fb: FormBuilder,
              private location: Location) {
    this.repoForm = fb.group({
      name: ['', [Validators.required, forbiddenNameValidator('new')]]
    });
  };

  ngOnInit(): void {
    this.route.url.subscribe((value) => {
        if (value[value.length - 1].path === 'new') {
          this.mode = 'new'
        }
      }
    );
    this.route.params.subscribe((params) => {
      this.loadRepo(params['repoName']);
    });
  }

  private loadRepo(repoName: string): void {
    if (Validator.notEmpty(repoName) && this.mode === 'edit') {
      this.repoHttp.getRepo(repoName).subscribe((result) => {
        this.repo = result;
        this.repoForm.controls.name.setValue(result.name);
        this.loadPackages();
      },
      (err) => {
        this.router.navigate(['/not-found', 'repo', repoName]);
      });
    }
  }

  private loadPackages(): void {
    if (this.repo && Validator.notEmpty(this.repo.name)) {
      this.packageHttp.getVersionsOfRepo(this.repo.name).subscribe(
        (result) => this.packages = result.sort(Sorter.packageVersion)
      );
    }
  }

  public save(formValue): void {
    const repo = this.repo;
    repo.name = formValue.name;

    let check;
    if (this.mode === 'new') {
      check = this.repoHttp.existsRepo(repo.name);
    } else {
      check = Observable.of(false);
    }

    check.flatMap(exists => {
      if (exists) {
        return Observable.throw(`Repository named '${repo.name}' already exists!`);
      } else {
        return this.doSave(repo);
      }
    }).subscribe(
      () => {
        this.alerts.success(`Successfully saved repository '${repo.name}'.`);
        if (this.mode === 'new') {
          this.router.navigate(['/repo']);
        }
      },
      (err) => {
        this.alerts.danger(`Error saving repository '${repo.name}': ${err}`);
        console.error(err);
      }
    );
  }

  public addMirror(): void {
    const repo = this.repo;
    repo.name = this.repoForm.controls.name.value;
    this.doSave(repo).subscribe(() => {
      this.router.navigate(['/repo', this.repo.name, 'mirror', 'new']);
    });
  }

  public editMirror(id: number): void {
    const repo = this.repo;
    repo.name = this.repoForm.controls.name.value;
    this.doSave(repo).subscribe(() => this.router.navigate(['/repo', this.repo.name, 'mirror', id]));
  }

  public deleteMirror(id: number): void {
    if (Validator.idIsSet(id)) {
      this.mirrorHttp.deleteMirror(id.toString()).subscribe(
        () => {
          this.repo.mirrors.forEach(function (item, index, object) {
            if (item.id === id) {
              object.splice(index, 1);
            }
          });
        });
    }
  }

  public setPrimary(id: number): void {
    this.repo.primaryMirror = id;
  }

  public gotToPackage(pkg: PackageVersion): void {
    if (pkg) {
      this.router.navigate(['/package', pkg.name]);
    }
  }

  private doSave(repo: Repo): Observable<Repo> {
    let call: Observable<Repo>;
    if (Validator.idIsSet(repo.id)) {
      call = this.repoHttp.editRepo(repo);
    } else {
      call = this.repoHttp.newRepo(repo);
    }

    return call;
  }

  public goBack(): void {
    this.location.back();
  }

  public forceReindex() {
    this.repoHttp.forceReindex(this.repo).subscribe(() => {});
  }
}
