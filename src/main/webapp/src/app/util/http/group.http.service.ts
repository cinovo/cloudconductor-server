
import {of as observableOf,  Observable } from 'rxjs';

import {catchError, map} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { User } from './user.http.service';

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

  private static readonly emptyGroup: Group = {name: '', description: '', permissions: []};

  private _basePath = 'api/usergroup'

  public static getEmptyGroup(): Group {
    return Object.assign({}, GroupHttpService.emptyGroup);
  }

  constructor(private http: HttpClient) { }

  public getGroups(): Observable<Group[]> {
    return this.http.get<Group[]>(this._basePath);
  }

  public getGroupNames(): Observable<string[]> {
    return this.getGroups().pipe(map(groups => groups.map(g => g.name)));
  }

  public getGroup(groupName: string): Observable<Group> {
    return this.http.get<Group>(`${this._basePath}/${groupName}`);
  }

  public existsGroup(groupName: string): Observable<boolean> {
    return this.getGroup(groupName).pipe(
                map((g) => g.name && g.name.length > 0),
                catchError(err => observableOf(false)),);
  }

  public getMembers(groupName: string): Observable<User[]> {
    return this.http.get<User[]>(`${this._basePath}/${groupName}/members`);
  }

  public saveGroup(groupToSave: Group): Observable<boolean> {
    groupToSave['@class'] = 'de.cinovo.cloudconductor.api.model.UserGroup';
    return this.http.put<boolean>(this._basePath, groupToSave);
  }

  public deleteGroup(groupName: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePath}/${groupName}`)
  }

}
