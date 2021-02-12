import { Component, Input, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { Group, GroupHttpService } from '../util/http/group.http.service';
import { User } from '../util/http/user.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'group-member',
  templateUrl: './group.member.comp.html'
})
export class GroupMemberComponent implements OnInit {

  @Input() groupObs: Observable<Group>

  public groupMembers: User[];

  constructor(private readonly groupHttp: GroupHttpService) {  }

  ngOnInit(): void {
    this.groupObs.pipe(mergeMap((group) => this.groupHttp.getMembers(group.name)))
      .subscribe(
        (members) => this.groupMembers = members,
        (err) => console.error(err)
      );
  }

}
