import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';

import { Sorter } from '../../util/sorters.util';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface AdditionalLink {
  id?: number;
  label: string;
  url: string;
}

@Injectable()
export class AdditionalLinkHttpService {

  private _linksData: BehaviorSubject<AdditionalLink[]> = new BehaviorSubject([]);
  public links: Observable<AdditionalLink[]> = this._linksData.asObservable();

  private reloading = false;

  private basePathURL = 'api/links'

  constructor(private http: HttpClient) {
    this.reloadLinks();
  }

  public getLinks(): Observable<AdditionalLink[]> {
    return this.http.get<AdditionalLink[]>(this.basePathURL).share();
  }

  public deleteLink(id: number): Observable<boolean> {
    let res = this.http.delete<boolean>(`${this.basePathURL}/${id.toString()}`).share();
    res.subscribe(() => this.reloadLinks(), () => {});
    return res;
  }

  public newLink(link: AdditionalLink): Observable<AdditionalLink> {
    link['@class'] = 'de.cinovo.cloudconductor.api.model.AdditionalLink';
    let res = this.http.post<AdditionalLink>(this.basePathURL, link).share();
    res.subscribe(() => this.reloadLinks(), () => {});
    return res;
  }

  public editLink(link: AdditionalLink): Observable<AdditionalLink> {
    link['@class'] = 'de.cinovo.cloudconductor.api.model.AdditionalLink';
    let res = this.http.put<AdditionalLink>(this.basePathURL, link).share();
    res.subscribe(() => this.reloadLinks(), () => {});
    return res;
  }

  private reloadLinks() {
    if (!this.reloading) {
      this.reloading = true;
      this.getLinks().subscribe(
        (result) => {
          this._linksData.next(result.sort(Sorter.links));
          this.reloading = false;
        }, (err) => {
          console.error(err);
        }
      );
    }
  }
}
