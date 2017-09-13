import { Component, OnInit, OnDestroy } from '@angular/core';

import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';

import { AlertService } from '../util/alert/alert.service';
import { ConfigFile } from '../util/http/config-file.model';
import { FileHttpService } from '../util/http/file.http.service';
import { Sorter } from '../util/sorters.util';
import { TemplateHttpService, Template } from '../util/http/template.http.service';
import { Validator } from '../util/validator.util';

@Component({
  templateUrl: './file.overview.comp.html'
})
export class FileOverviewComponent implements OnInit, OnDestroy {

  private _searchQuery = '';
  private _searchTemplateQuery = '';

  private _filesSub: Subject<ConfigFile[]> = new BehaviorSubject<ConfigFile[]>([]);
  public files: Observable<ConfigFile[]> = this._filesSub.asObservable();
  public templates: Template[] = [];

  private filesSub: Subscription;
  private templateSub: Subscription;

  private static filterData(file: ConfigFile, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return file.name.indexOf(query.trim()) >= 0;
    }
    return true;
  }

  private static filterTemplateData(file: ConfigFile, templateQuery: string) {
    if (Validator.notEmpty(templateQuery)) {
      return file.templates.some(t => t === templateQuery);
    }
    return true;
  }

  constructor(private alertService: AlertService,
              private fileHttp: FileHttpService,
              private templateHttp: TemplateHttpService) { }

  ngOnInit(): void {
    this.reloadData();
  }

  ngOnDestroy(): void {
    if (this.filesSub) {
      this.filesSub.unsubscribe();
    }

    if (this.templateSub) {
      this.templateSub.unsubscribe();
    }
  }

  get searchQuery() {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.reloadData();
  }

  get searchTemplateQuery() {
    return this._searchTemplateQuery;
  }

  set searchTemplateQuery(value: string) {
    this._searchTemplateQuery = value;
    this.reloadData();
  }

  private reloadData(): void {
    this.filesSub = this.fileHttp.getFiles().subscribe((files) => {
      const filteredFiles = files.filter(f => FileOverviewComponent.filterData(f, this._searchQuery))
                                  .filter(f => FileOverviewComponent.filterTemplateData(f, this._searchTemplateQuery))
                                  .sort(Sorter.files);
      this._filesSub.next(filteredFiles);
    });

    this.templateSub = this.templateHttp.getTemplates().subscribe((result) => {
      this.templates = result;
    });
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
