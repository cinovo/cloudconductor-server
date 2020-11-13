import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable,  Subject, Subscription } from 'rxjs';

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
  public mode: Mode;

  constructor(private groupHttp: GroupHttpService,
              private route: ActivatedRoute,
              private router: Router,
              private alertService: AlertService) { }

  ngOnInit(): void {
    this._routeSub = this.route.paramMap.subscribe((paraMap) => {
      const groupName = paraMap.get('groupName');
      if (groupName) {
        this.groupName = groupName;
        this.mode = this.modes.EDIT
        this.reloadGroup();
      } else {
        this.mode = this.modes.NEW;
      }
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
        if (err.status && err.status === 404) {
          this.router.navigate(['/not-found', 'group', this.groupName]);
          return;
        }
        this.alertService.danger(`Error loading group '${this.groupName}'!`);
        console.error(err);
      }
    );
  }

  public deleteGroup(): void {
    this.groupHttp.deleteGroup(this.groupName).subscribe(
      () => {
        this.alertService.success(`Successfully deleted user group '${this.groupName}'.`);
        this.router.navigate(['/group']);
      }, (err) => {
        this.alertService.danger(`Error deleting user group '${this.groupName}'!`);
        console.error(err);
      }
    );
  }
}
