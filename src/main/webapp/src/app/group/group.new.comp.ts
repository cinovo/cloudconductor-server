import { Component, OnInit } from '@angular/core';

import { Observable ,  ReplaySubject ,  Subject } from 'rxjs';

import { Group, GroupHttpService } from '../util/http/group.http.service';
import { Mode } from '../util/enums.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './group.new.comp.html'
})
export class GroupNewComponent implements OnInit {

  private _groupSubject: Subject<Group> = new ReplaySubject(1);
  public group$: Observable<Group> = this._groupSubject.asObservable();

  public modes = Mode;

  constructor(private groupHttp: GroupHttpService) { }

  ngOnInit(): void {
    this._groupSubject.next(GroupHttpService.getEmptyGroup());
  }

}
