import { Component, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs';

import { AlertService } from '../util/alert/alert.service';
import { Validator } from '../util/validator.util';
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
export class RepoEdit implements AfterViewInit {

  public mode = 'edit';
  public repo: Repo = {name: '', mirrors: [], primaryMirror: 0};

  public packages: Array<PackageVersion> = [];

  constructor(private repoHttp: RepoHttpService, private mirrorHttp: RepoMirrorHttpService,
              private packageHttp: PackageHttpService,
              private route: ActivatedRoute, private alerts: AlertService, private router: Router) {
  };

  ngAfterViewInit(): void {
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
        this.loadPackages();
      });
    }
  }

  private loadPackages(): void {
    if (this.repo && Validator.notEmpty(this.repo.name))
      this.packageHttp.getVersionsOfRepo(this.repo.name).subscribe(
        (result) => this.packages = result.sort(Sorter.packageVersion)
      );

  }

  save(): void {
    this.doSave(() => {
      this.alerts.success('Successfully saved your repository');
      if (this.mode === 'new') {
        this.router.navigate(['repo']);
      }
    });
  }

  addMirror(): void {
    this.doSave(() => {
      this.router.navigate(['repo', this.repo.name, 'mirror', 'new']);
    });
  }

  editMirror(id: number): void {
    this.doSave(() => this.router.navigate(['repo', this.repo.name, 'mirror', id]));
  }

  deleteMirror(id: number): void {
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

  setPrimary(id: number): void {
    this.repo.primaryMirror = id;
  }

  gotToPackage(pkg: PackageVersion): void {
    if (pkg) {
      this.router.navigate(['/package', pkg.name], {queryParams: {ret: 'repoDetail', id: this.repo.name}})
    }
  }

  private fieldValidation(): boolean {
    let error = false;
    if (!this.isNameValid()) {
      this.alerts.danger('Please insert a repo name.');
      error = true;
    }
    return error;
  }

  public isNameValid(): boolean {
    return Validator.notEmpty(this.repo.name);
  }

  private doSave(successCallback: (repo: Repo) => any): void {
    if (this.fieldValidation()) {
      return;
    }
    let call: Observable<Repo>;
    if (Validator.idIsSet(this.repo.id)) {
      call = this.repoHttp.editRepo(this.repo);
    } else {
      call = this.repoHttp.newRepo(this.repo);
    }
    call.subscribe(
      (result) => {
        if (successCallback) {
          successCallback(result);
        }
      },
      (error) => {
        this.alerts.danger('Failed to save the repository');
      }
    );
  }
}
