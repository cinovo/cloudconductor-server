import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { ConfigFile, FileHttpService } from '../util/http/file.http.service';
import { FileForm, FileType } from './file.detail.comp';

@Injectable()
export class FileResolver implements Resolve<FileForm> {

  constructor(private fileHttpService: FileHttpService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<FileForm> {
    const fileName = route.paramMap.get('fileName');

    const emptyFileForm: FileForm = {
      name: '', targetPath: '', owner: '', group: '', fileMode: '644', fileContent: '',
      isTemplate: false, type: FileType.File, dependentServices: [], templates: [], servicesReload: []
    };

    if (fileName && fileName.length > 0) {
      return this.fileHttpService.getFile(fileName).flatMap((file) => {
        return this.fileHttpService.getFileData(file.name).map((data) => {

          // convert into form
          const fType = (file.isDirectory) ? FileType.Directory : FileType.File;
          const fileForm = {...file, fileContent: data, servicesReload: [], type: fType};
          delete fileForm['@class'];
          delete fileForm['checksum'];
          delete fileForm['isDirectory']
          delete fileForm['isReloadable']
          return fileForm;
        });
      });
    } else {
      return Observable.of(emptyFileForm);
    }
  }

}
