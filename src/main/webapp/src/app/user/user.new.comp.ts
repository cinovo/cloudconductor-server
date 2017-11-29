import { Component, OnInit } from '@angular/core';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';

import { UserHttpService, User } from '../util/http/user.http.service';
import { Mode } from '../util/enums.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './user.new.comp.html'
})
export class UserNewComponent implements OnInit {

  private _userSubject: BehaviorSubject<User> = new BehaviorSubject(UserHttpService.emptyUser);
  public user$: Observable<User> = this._userSubject.asObservable();

  public modes = Mode;

  constructor(userHttp: UserHttpService) { }

  ngOnInit(): void {
    this._userSubject.next(UserHttpService.emptyUser)
  }

}
