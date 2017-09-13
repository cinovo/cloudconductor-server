import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { FileHttpService } from '../util/http/file.http.service';
import { FileForm } from '../util/http/config-file.model';

@Injectable()
export class FileResolver implements Resolve<FileForm> {

  constructor(private fileHttpService: FileHttpService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<FileForm> {
    const fileName = route.paramMap.get('fileName');

    const emptyFileForm = new FileForm();

    if (fileName && fileName.length > 0) {
      return this.fileHttpService.getFile(fileName).flatMap((file) => {
        return this.fileHttpService.getFileData(file.name).map((data) => {
          return file.toForm();
        });
      });
    } else {
      return Observable.of(emptyFileForm);
    }
  }

}
