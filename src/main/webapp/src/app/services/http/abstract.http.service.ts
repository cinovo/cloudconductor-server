/**
 * Created by psigloch on 03.11.2016.
 */
import { Http, Response, Headers } from "@angular/http";
import { Observable } from "rxjs";

import 'rxjs/add/operator/toPromise';

export abstract class HTTPService {

  private apiURL = 'api/';

  protected basePathURL = '';

  protected headers = new Headers(
    {'Content-Type': 'application/json'}
  );

  constructor(protected http: Http) {
  }

  protected _get(pathUrl: string): Observable<any> {
    return this.http
      .get(this.target(pathUrl))
      .map(this.extractData)
      .catch(this.handleError);
  }

  protected _post(pathUrl:string, data:any):Observable<any> {
    return this.http
      .post(this.target(pathUrl), JSON.stringify(data), {headers: this.headers})
      .map(this.extractData)
      .catch(this.handleError);
  }

  protected _put(pathUrl:string, data:any):Observable<any> {
    return this.http
      .put(this.target(pathUrl), JSON.stringify(data), {headers: this.headers})
      .map(() => data)
      .catch(this.handleError);
  }

  protected _delete(pathUrl:string):Observable<boolean> {
    return this.http
      .delete(this.target(pathUrl), {headers: this.headers})
      .map(() => true)
      .catch(this.handleError);
  }

  protected extractData(res: Response):any {
    let body:any = res.json();
    return body || {};
  }

  private target(pathUrl:string):string {
    return this.apiURL + this.basePathURL + pathUrl;
  }
  private handleError(error: Response | any) {
    let errMsg: string;
    if (error instanceof Response) {
      const body = error.json() || '';
      const err = body.error || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    console.error(errMsg);
    return Observable.throw(errMsg);
  }
}
