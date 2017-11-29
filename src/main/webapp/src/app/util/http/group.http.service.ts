import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
export interface Group {
  name: string,
  description: string,
  permissions: string[]
}

@Injectable()
export class GroupHttpService {

  public static emptyGroup: Group = {name: '', description: '', permissions: []};

  private _basePath = 'api/usergroup'

  constructor(private http: HttpClient) { }

  getGroups(): Observable<Group[]> {
    return this.http.get<Group[]>(this._basePath);
  }

  getGroupNames(): Observable<string[]> {
    return this.getGroups().map(groups => groups.map(g => g.name));
  }

  getGroup(groupName: string): Observable<Group> {
    return this.http.get<Group>(`${this._basePath}/${groupName}`);
  }

  saveGroup(groupToSave: Group): Observable<boolean> {
    return this.http.put<boolean>(this._basePath, groupToSave);
  }

  deleteGroup(groupName: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePath}/${groupName}`)
  }

}
