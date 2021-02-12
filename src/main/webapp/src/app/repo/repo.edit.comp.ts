import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { throwError as observableThrowError, Observable, of} from 'rxjs';
import { mergeMap } from 'rxjs/operators';

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

  constructor(private readonly repoHttp: RepoHttpService,
              private readonly mirrorHttp: RepoMirrorHttpService,
              private readonly packageHttp: PackageHttpService,
              private readonly route: ActivatedRoute,
              private readonly alerts: AlertService,
              private readonly router: Router,
              private readonly fb: FormBuilder,
              private readonly location: Location) {
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
    repo.name = formValue.name.trim();

    this.doSave(repo)
    .subscribe(
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
    const newEmptyRepo = {...this.repo, name: this.repoForm.controls.name.value};

    this.doSave(newEmptyRepo).subscribe(
      () => this.router.navigate(['/repo', newEmptyRepo.name, 'mirror', 'new']),
      (err) => {
        this.alerts.danger(`Error saving repository '${newEmptyRepo.name}': ${err}`);
        console.error(err);
      });
  }

  public editMirror(id: number): void {
    const updatedRepo = {...this.repo, name: this.repoForm.controls.name.value};

    this.doSave(updatedRepo).subscribe(
      () => this.router.navigate(['/repo', updatedRepo.name, 'mirror', id]),
      (err) => console.error(err)
    );
  }

  public deleteMirror(id: number): void {
    if (Validator.idIsSet(id)) {
      this.mirrorHttp.deleteMirror(id.toString())
      .subscribe(
        () => {
          this.repo.mirrors.forEach(function (item, index, object) {
            if (item.id === id) {
              object.splice(index, 1);
            }
          });
        }
      );
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
    return this.checkIfNecessary(repo).pipe(
      mergeMap(exists => {
        if (exists) {
          return observableThrowError(`Repository named '${repo.name}' already exists!`);
        }

        if (Validator.idIsSet(repo.id)) {
          return this.repoHttp.editRepo(repo);
        }

        return this.repoHttp.newRepo(repo);
      })
    );
  }

  private checkIfNecessary(repo: Repo): Observable<boolean> {
    if (this.mode !== 'new') {
      return of(false);
    }

    return this.repoHttp.existsRepo(repo.name)
  }

  public goBack(): void {
    this.location.back();
  }

  public forceReindex() {
    this.repoHttp.forceReindex(this.repo).subscribe(
      () => this.alerts.success(`Successfully scheduled manual reindex of repo '${this.repo.name}'.`),
      (err) => {
        this.alerts.danger(`Error forcing reindex of repo '${this.repo.name}': ${err}`);
        console.log(err)
      }
    );
  }
}
