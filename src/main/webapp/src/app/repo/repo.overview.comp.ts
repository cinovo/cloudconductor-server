import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AlertService } from '../util/alert/alert.service';
import { Sorter } from '../util/sorters.util';
import { Validator } from '../util/validator.util';
import { Repo, RepoHttpService } from '../util/http/repo.http.service';
import { RepoMirrorHttpService, RepoMirror } from '../util/http/repomirror.http.service';

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'repo-overview',
  templateUrl: './repo.overview.comp.html'
})
export class RepoOverview implements OnInit {

  private _searchQuery: string = null;

  private _repos: Array<Repo> = [];

  public reposLoaded = false;

  private static filterData(repo: Repo, query: string) {
    if (Validator.notEmpty(query)) {
      return repo.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  constructor(private repoHttp: RepoHttpService,
              private mirrorHttp: RepoMirrorHttpService,
              private router: Router,
              private alertService: AlertService) { };

  public ngOnInit(): void {
    this.loadData();
  }

  private loadData() {
    this.repoHttp.getRepos().subscribe((result) => {
      this.repos = result;
      this.reposLoaded = true;
    }, (err) => {
      this.alertService.danger('Error loading repositories!');
      console.error(err);
      this.reposLoaded = true;
    });
  }

  deleteRepo(repoName: string) {
    if (Validator.notEmpty(repoName)) {
      this.repoHttp.deleteRepo(repoName).subscribe((result) => {
        this.loadData();
      }, (err) => {
        this.alertService.danger(`Error deleting repository '${repoName}'!`);
        console.error({err});
      });
    }
  }

  deleteMirror(id: number): void {
    if (Validator.idIsSet(id)) {
      this.mirrorHttp.deleteMirror(id.toString()).subscribe((result) => this.loadData());
    }
  }

  get repos(): Array<Repo> {
    return this._repos;
  }

  set repos(value: Array<Repo>) {
    this._repos = value
      .filter(repo => RepoOverview.filterData(repo, this._searchQuery))
      .sort(Sorter.repo);

    for (let repo of this._repos) {
      repo.mirrors = repo.mirrors.sort(Sorter.mirror);
    }
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadData();
  }

  goToMirror(mirror: RepoMirror) {
    if (mirror) {
      this.router.navigate(['repo', mirror.repo, 'mirror', mirror.id]);
    }
  }

  goToDetail(repo: Repo) {
    if (repo) {
      this.router.navigate(['repo', repo.name]);
    }
  }

}
