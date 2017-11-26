import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

import { ConfigFile, FileForm, FileType } from './config-file.model';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class FileHttpService {

  private _basePathURL = 'api/file';

  constructor(private http: HttpClient) { }

  public getFiles(): Observable<ConfigFile[]> {
    return this.http.get<ConfigFile[]>(this._basePathURL);
  }

  public getFilesForTemplate(templateName: string): Observable<ConfigFile[]> {
    return this.http.get<ConfigFile[]>(`api/template/${templateName}`);
  }

  public updateFile(updatedFile: ConfigFile): Observable<boolean> {
    updatedFile['@class'] = 'de.cinovo.cloudconductor.api.model.ConfigFile';
    return this.http.put<boolean>(this._basePathURL, updatedFile);
  }

  public getFile(fileName: string): Observable<ConfigFile> {
    return this.http.get<ConfigFile>(`${this._basePathURL}/${fileName}`)
                    .map(file => Object.assign(new ConfigFile(), file));
  }

  public existsFile(fileName: string): Observable<boolean> {
    return this.getFile(fileName)
              .map(f => (f !== undefined && f.name !== undefined))
              .catch(err => Observable.of(false));
  }

  public deleteFile(fileName: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${fileName}`);
  }

  public getFileData(fileName: string): Observable<string> {
    return this.http.get<string>(`${this._basePathURL}/${fileName}/data`);
  }

  public updateFileData(fileName: string, fileData: string): Observable<boolean> {
    return this.http.put<boolean>(`${this._basePathURL}/${fileName}/data`, fileData);
  }

}
