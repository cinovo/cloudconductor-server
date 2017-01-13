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

export interface AdditionalLink {
  id?: number;
  label:string;
  url:string;
}

@Injectable()
export class AdditionalLinkHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'links/';
  }

  public getLinks():Observable<Array<AdditionalLink>> {
    return this._get("");
  }

  public getLink(id:number):Observable<AdditionalLink> {
    return this._get(id.toString());
  }

  public deleteLink(id:number): Observable<boolean> {
    return this._delete(id.toString());
  }

  public new(link: AdditionalLink):Observable<AdditionalLink> {
    link['@class'] = "de.cinovo.cloudconductor.api.model.AdditionalLink";
    return this._post("", link);
  }


  public edit(link: AdditionalLink):Observable<AdditionalLink> {
    link['@class'] = "de.cinovo.cloudconductor.api.model.AdditionalLink";
    return this._put("", link);
  }

}
