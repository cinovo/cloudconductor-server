import { HTTPService } from "./abstract.http.service";
import { Injectable } from "@angular/core";
import { Http } from "@angular/http";
import { Observable } from "rxjs";
/**
 * Created by psigloch on 11.01.2017.
 */

export interface Package {
  id?:number;
  name:string;
}

@Injectable()
export class PackageHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'package/';
  }

  public getPackages(): Observable<Array<Package>> {
    return this._get("");
  }

}
