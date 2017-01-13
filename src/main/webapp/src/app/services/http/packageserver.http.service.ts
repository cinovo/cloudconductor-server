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
import { PackageServerGroup } from "./packageservergroup.http.service";

export interface PackageServer {
  id?: number;
  serverGroup: number;
  path: string;
  description: string;
  indexerType?: string;
  providerType?: string;
  basePath?: string;
  bucketName?: string;
  awsRegion?: string;
  accessKeyId?: string;
  secretKey?: string;
}

@Injectable()
export class PackageServerHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'packageserver/';
  }

  public getPackageServer(id:string): Observable<PackageServer> {
    return this._get(id);
  }

  public deletePackageServer(id: string): Observable<boolean> {
    return this._delete(id);
  }

  public newPackageServer(server: PackageServer):Observable<PackageServer> {
    server['@class'] = "de.cinovo.cloudconductor.api.model.PackageServer";
    return this._post("", server);
  }


  public editPackageServer(server: PackageServer):Observable<PackageServer> {
    server['@class'] = "de.cinovo.cloudconductor.api.model.PackageServer";
    return this._put("", server);
  }
}
