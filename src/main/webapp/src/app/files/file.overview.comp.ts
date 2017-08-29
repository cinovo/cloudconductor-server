import { Component, OnInit, OnDestroy } from '@angular/core';

import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';

import { AlertService } from '../util/alert/alert.service';
import { FileHttpService, ConfigFile } from '../util/http/file.http.service';
import { Validator } from '../util/validator.util';

@Component({
  templateUrl: './file.overview.comp.html'
})
export class FileOverviewComponent implements OnInit, OnDestroy {

  private _searchQuery = '';

  private _filesSub: Subject<ConfigFile[]> = new BehaviorSubject<ConfigFile[]>([]);
  public files: Observable<ConfigFile[]> = this._filesSub.asObservable();

  private filesSub: Subscription;

  private static filterData(file: ConfigFile, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return file.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  constructor(private alertService: AlertService,
              private fileHttp: FileHttpService) { }

  ngOnInit(): void {
    this.reloadData();
  }

  ngOnDestroy(): void {
    if (this.filesSub) {
      this.filesSub.unsubscribe();
    }
  }

  private reloadData(): void {
    this.filesSub = this.fileHttp.getFiles().subscribe((files) => {
      const filteredFiles = files.filter(f => FileOverviewComponent.filterData(f, this._searchQuery));
      this._filesSub.next(filteredFiles);
    });
  }

  get searchQuery() {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.reloadData();
  }

  public deleteFile(fileToDelete: ConfigFile) {
    this.fileHttp.deleteFile(fileToDelete.name).subscribe(() => {
      this.alertService.success(`File '${fileToDelete}' successfully deleted!`);
      this.reloadData();
    },
    (err) => {
      console.error(err);
    });
  }

}
