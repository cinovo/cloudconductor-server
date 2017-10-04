import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { BehaviorSubject, Observable } from 'rxjs';

import { HTTPService } from './abstract.http.service';
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
export class AdditionalLinkHttpService extends HTTPService {

  private _linksData: BehaviorSubject<Array<AdditionalLink>> = new BehaviorSubject([]);
  public links: Observable<Array<AdditionalLink>> = this._linksData.asObservable();

  private reloading = false;

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'links/';
    this.reloadLinks();
  }

  public getLinks(): Observable<Array<AdditionalLink>> {
    return this._get('');
  }

  public deleteLink(id: number): Observable<boolean> {
    let res = this._delete(id.toString());
    res.subscribe(() => this.reloadLinks(), () => {});
    return res;
  }

  public newLink(link: AdditionalLink): Observable<AdditionalLink> {
    link['@class'] = 'de.cinovo.cloudconductor.api.model.AdditionalLink';
    let res = this._post('', link);
    res.subscribe(() => this.reloadLinks(), () => {});
    return res;
  }


  public editLink(link: AdditionalLink): Observable<AdditionalLink> {
    link['@class'] = 'de.cinovo.cloudconductor.api.model.AdditionalLink';
    let res = this._put('', link).share();
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
        }
      );
    }
  }
}
