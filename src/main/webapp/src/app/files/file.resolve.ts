
import {of as observableOf, throwError as observableThrowError,  Observable } from 'rxjs';

import {map, catchError, mergeMap} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';

import { FileHttpService } from '../util/http/file.http.service';
import { FileForm } from '../util/http/config-file.model';

@Injectable()
export class FileResolver implements Resolve<FileForm> {

  constructor(private fileHttpService: FileHttpService,
              private router: Router) { }

  resolve(route: ActivatedRouteSnapshot): Observable<FileForm> {
    const fileName = route.paramMap.get('fileName');

    const emptyFileForm = new FileForm();

    if (fileName && fileName.length > 0) {
      return this.fileHttpService.getFile(fileName).pipe(mergeMap((file) => {
        return this.fileHttpService.getFileData(file.name).pipe(map((data) => {
          const fileForm = file.toForm();
          fileForm.fileContent = data;
          return fileForm;
        }));
      }),catchError(err => {
        this.router.navigate(['/not-found', 'file', fileName]);
        return observableThrowError(err);
      }),);
    } else {
      return observableOf(emptyFileForm);
    }
  }

}
