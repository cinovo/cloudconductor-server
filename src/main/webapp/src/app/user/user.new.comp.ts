import { UserHttpService } from '../util/http/user.http.service';
import { Component, OnInit } from '@angular/core';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Mode } from '../util/enums.util';

@Component({
  templateUrl: './user.new.comp.html'
})
export class UserNewComponent implements OnInit {

  private _userSubject = new BehaviorSubject({});
  public user$ = this._userSubject.asObservable();

  public modes = Mode;

  constructor(userHttp: UserHttpService) { }

  ngOnInit(): void {
    this._userSubject.next(UserHttpService.emptyUser)
  }

}
