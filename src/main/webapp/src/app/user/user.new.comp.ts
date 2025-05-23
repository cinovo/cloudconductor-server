import { Component, OnInit } from '@angular/core';

import { Observable, Subject, ReplaySubject } from 'rxjs';

import { UserHttpService, User } from '../util/http/user.http.service';
import { Mode } from '../util/enums.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
    templateUrl: './user.new.comp.html',
    standalone: false
})
export class UserNewComponent implements OnInit {

  private _userSubject: Subject<User> = new ReplaySubject(1);
  public user$: Observable<User> = this._userSubject.asObservable();

  public modes = Mode;

  constructor() { }

  ngOnInit(): void {
    const emptyUser = UserHttpService.getEmptyUser();
    this._userSubject.next(emptyUser);
  }

}
