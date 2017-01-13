/**
 * Created by psigloch on 04.11.2016.
 */
/**
 * Created by psigloch on 03.11.2016.
 */
import { Injectable }     from '@angular/core';
import { Http } from '@angular/http';
import { Observable }     from 'rxjs/Observable';
import { HTTPService } from "./abstract.http.service";

export interface PackageServerGroup {
  id: number;
  name:string;
  packageServers:Array<PackageServer>;
  primaryServer:number;
}

export interface PackageServer {
  basePath:string;
  description:string;
  id:number;
  indexerType:string;
  path:string;
  providerType:string;
  serverGroup: number;
}

@Injectable()
export class PackageServerHttpService extends HTTPService{

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'packageservergroup/';
  }

  public getGroups(): Observable<Array<PackageServerGroup>> {
    return this._get('');
  }

}
