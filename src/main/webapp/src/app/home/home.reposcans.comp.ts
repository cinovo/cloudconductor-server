import { Component, Input, Output, EventEmitter } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { Repo } from '../util/http/repo.http.service';
import { RepoScansService } from '../util/reposcans/reposcans.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'home-reposcans',
  templateUrl: 'home.reposcans.comp.html'
})
export class HomeRepoScansComponent {

  @Input() reposObs: Observable<Repo[]>;
  @Output() onRepoClicked: EventEmitter<string> = new EventEmitter<string>();

  constructor(public repoScans: RepoScansService) { }

  public repoClicked(repo: Repo): void {
    this.onRepoClicked.emit(repo.name);
  }

}
