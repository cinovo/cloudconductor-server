/**
 * Created by psigloch on 04.11.2016.
 */
/**
 * Created by psigloch on 03.11.2016.
 */
import { Injectable } from "@angular/core";
import { Http } from "@angular/http";
import { Observable } from "rxjs/Observable";
import { HTTPService } from "./abstract.http.service";
import { PackageServer } from "./packageserver.http.service";

export interface PackageServerGroup {
  id?: number;
  name: string;
  packageServers: Array<PackageServer>;
  primaryServer?: number;
}

@Injectable()
export class PackageServerGroupHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'packageservergroup/';
  }

  public getGroups(): Observable<Array<PackageServerGroup>> {
    return this._get('');
  }

  public getGroup(groupId: string): Observable<PackageServerGroup> {
    return this._get(groupId);
  }

  public deleteGroup(groupId: string): Observable<boolean> {
    return this._delete(groupId);
  }

  public newGroup(group: PackageServerGroup):Observable<PackageServerGroup> {
    group['@class'] = "de.cinovo.cloudconductor.api.model.PackageServerGroup";
    return this._post("", group);
  }


  public editGroup(group: PackageServerGroup):Observable<PackageServerGroup> {
    group['@class'] = "de.cinovo.cloudconductor.api.model.PackageServerGroup";
    return this._put("", group);
  }

}
