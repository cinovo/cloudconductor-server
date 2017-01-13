/**
 * Created by psigloch on 03.11.2016.
 */
import { Injectable }     from '@angular/core';
import { Http } from '@angular/http';
import { Observable }     from 'rxjs/Observable';
import { HTTPService } from "./abstract.http.service";

export interface KeyValue {
  key:string;
  value:any;
}

@Injectable()
export class ConfigHttpService extends HTTPService{

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'config/';
  }

  public getConfig(templateName?:string): Observable<Map<string, string>> {
    var res:Observable<Map<string, string>> = this._get(templateName);
    return res;
  }


}
