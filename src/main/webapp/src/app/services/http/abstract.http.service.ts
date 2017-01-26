import { Http, Response, Headers } from "@angular/http";
import { Observable } from "rxjs";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
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
      .map(HTTPService.extractData)
      .catch(HTTPService.handleError)
      .share();
  }

  protected _post(pathUrl: string, data: any): Observable<any> {
    return this.http
      .post(this.target(pathUrl), JSON.stringify(data), {headers: this.headers})
      .map(HTTPService.extractData)
      .catch(HTTPService.handleError)
      .share();
  }

  protected _put(pathUrl: string, data: any): Observable<any> {
    return this.http
      .put(this.target(pathUrl), JSON.stringify(data), {headers: this.headers})
      .map((response) => {
        let result = HTTPService.extractData(response);
        if (Object.keys(result).length === 0) {
          return data;
        }
        return result;
      })
      .catch(HTTPService.handleError)
      .share();
  }

  protected _delete(pathUrl: string): Observable<any> {
    return this.http.delete(this.target(pathUrl), {headers: this.headers})
      .map(HTTPService.extractData)
      .catch(HTTPService.handleError)
      .share();
  }

  protected static extractData(res: Response): any {
    try {
      return res.json();
    } catch (error) {
      return {};
    }
  }

  private target(pathUrl: string): string {
    return this.apiURL + this.basePathURL + pathUrl;
  }

  private static handleError(error: Response | any) {
    let errMsg: string;
    if (error instanceof Response) {
      errMsg = `${error.status} - ${error.statusText || ''} ${error}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    return Observable.throw(errMsg);
  }
}
