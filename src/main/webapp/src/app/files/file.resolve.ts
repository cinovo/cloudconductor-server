import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';

import { EMPTY } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';

import { FileHttpService } from '../util/http/file.http.service';
import { FileForm } from '../util/http/config-file.model';

export const fileResolver: ResolveFn<FileForm> = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const fileHttpService = inject(FileHttpService);
  const fileName = route.paramMap.get('fileName');

  return fileHttpService.getFile(fileName).pipe(
    mergeMap((file) => {
      if (file) {
        return fileHttpService.getFileData(file.name).pipe(map((data) => {
          const fileForm = file.toForm();
          fileForm.fileContent = data;
          return fileForm;
        }));
      } else {
        router.navigate(['/not-found', 'file', fileName]);
        return EMPTY;
      }
    })
  );
}
