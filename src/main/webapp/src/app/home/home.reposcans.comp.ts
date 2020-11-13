import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';

import { Observable ,  Subscription } from 'rxjs';

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
export class HomeRepoScansComponent implements OnInit, OnDestroy {

  public lastUpdate: number;

  @Input() reposObs: Observable<Repo[]>;
  @Output() onRepoClicked: EventEmitter<string> = new EventEmitter<string>();

  private repoSub: Subscription;

  constructor(public repoScans: RepoScansService) { }

  ngOnInit(): void {
    this.repoSub = this.reposObs.subscribe(() => {
      this.lastUpdate = new Date().getTime();
    });
  }

  ngOnDestroy(): void {
    if (this.repoSub) {
      this.repoSub.unsubscribe();
    }
  }

}
