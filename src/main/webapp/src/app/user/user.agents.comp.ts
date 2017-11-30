import { Component, Input } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { User } from '../util/http/user.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'user-agents',
  templateUrl: './user.agents.comp.html'
})
export class UserAgentsComponent {

  @Input() userObs: Observable<User>;

}
