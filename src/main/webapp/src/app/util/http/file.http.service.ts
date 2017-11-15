import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import { ConfigFile, FileForm, FileType } from './config-file.model';
import { HTTPService } from './abstract.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class FileHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'file/'
  }

  public getFiles(): Observable<ConfigFile[]> {
    return this._get('');
  }

  public getFilesForTemplate(templateName: string): Observable<ConfigFile[]> {
    return this._get(`template/${templateName}`);
  }

  public updateFile(updatedFile: ConfigFile): Observable<boolean> {
    updatedFile['@class'] = 'de.cinovo.cloudconductor.api.model.ConfigFile';
    return this._put('', updatedFile);
  }

  public getFile(fileName: string): Observable<ConfigFile> {
    return this._get(fileName).map((obj) => Object.assign(new ConfigFile(), obj));
  }

  public existsFile(fileName: string): Observable<boolean> {
    return this.getFile(fileName)
              .map(f => (f !== undefined && f.name !== undefined))
              .catch(err => Observable.of(false));
  }

  public deleteFile(fileName: string): Observable<boolean> {
    return this._delete(fileName);
  }

  public getFileData(fileName: string): Observable<string> {
    return this._get(`${fileName}/data`);
  }

  public updateFileData(fileName: string, fileData: string): Observable<boolean> {
    return this._put(`${fileName}/data`, fileData);
  }

}
