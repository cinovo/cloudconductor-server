import { Component, AfterViewInit } from "@angular/core";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";
import { Repo, RepoHttpService } from "../util/http/repo.http.service";
import { RepoMirrorHttpService, RepoMirror } from "../util/http/repomirror.http.service";
import { Router } from "@angular/router";

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  moduleId: module.id,
  selector: 'repo-overview',
  templateUrl: 'html/repo.overview.comp.html'
})
export class RepoOverview implements AfterViewInit {

  private _searchQuery: string = null;

  private _repos: Array<Repo> = [];

  constructor(private repoHttp: RepoHttpService, private mirrorHttp: RepoMirrorHttpService,
              private router: Router) {
  };

  ngAfterViewInit(): void {
    this.loadData();
  }

  private loadData() {
    this.repoHttp.getRepos().subscribe(
      (result) => this.repos = result
    )
  }

  deleteRepo(name: string) {
    if (Validator.notEmpty(name)) {
      this.repoHttp.deleteRepo(name).subscribe((result) => this.loadData());
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


  private static filterData(repo: Repo, query: string) {
    if (Validator.notEmpty(query)) {
      return repo.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

}
