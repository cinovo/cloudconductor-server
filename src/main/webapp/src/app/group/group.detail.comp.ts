import { AlertService } from '../util/alert/alert.service';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Group, GroupHttpService } from '../util/http/group.http.service';
import { Subscription } from 'rxjs';

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

  public group: Group;

  private _routeSub: Subscription;

  constructor(private groupHttp: GroupHttpService,
              private route: ActivatedRoute,
              private alertService: AlertService) { }

  ngOnInit(): void {
    this._routeSub = this.route.paramMap.subscribe((paraMap) => {
      const groupName = paraMap.get('groupName');

      if (groupName) {
        this.groupHttp.getGroup(groupName).subscribe(
          (group) => this.group = group,
          (err) => {
            this.alertService.danger(`Error loading group '${groupName}'!`);
            console.error(err);
          }
        );
      }
    });
  }

  ngOnDestroy(): void {
    if (this._routeSub) {
      this._routeSub.unsubscribe();
    }
  }
}
