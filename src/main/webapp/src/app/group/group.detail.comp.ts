import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { Group, GroupHttpService } from '../util/http/group.http.service';
import { Mode } from '../util/enums.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './group.detail.comp.html'
})
export class GroupDetailComponent implements OnInit, OnDestroy {

  private _groupSubject: Subject<Group> = new Subject();
  private _routeSub: Subscription;

  public groupName: string;
  public group$: Observable<Group> = this._groupSubject.asObservable();
  public modes = Mode;

  constructor(private groupHttp: GroupHttpService,
              private route: ActivatedRoute,
              private alertService: AlertService) { }

  ngOnInit(): void {
    this._routeSub = this.route.paramMap.subscribe((paraMap) => {
      this.groupName = paraMap.get('groupName');

      this.reloadGroup();
    });
  }

  ngOnDestroy(): void {
    if (this._routeSub) {
      this._routeSub.unsubscribe();
    }
  }

  public reloadGroup(): void {
    this.groupHttp.getGroup(this.groupName).subscribe(
      (group) => this._groupSubject.next(group),
      (err) => {
        this.alertService.danger(`Error loading group '${this.groupName}'!`);
        console.error(err);
      }
    );
  }
}
