import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { ConfigFile } from '../util/http/config-file.model';
import { FileHttpService } from '../util/http/file.http.service';
import { Sorter } from '../util/sorters.util';
import { TemplateHttpService, Template } from '../util/http/template.http.service';
import { Validator } from '../util/validator.util';
import { ServiceHttpService, Service } from '../util/http/service.http.service';

@Component({
  templateUrl: './file.overview.comp.html'
})
export class FileOverviewComponent implements OnInit, OnDestroy {

  private _searchQuery = '';
  private _templateQuery = '';
  private _serviceQuery = '';

  private _filesSub: Subject<ConfigFile[]> = new BehaviorSubject<ConfigFile[]>([]);
  public files: Observable<ConfigFile[]> = this._filesSub.asObservable();
  public templates: Template[] = [];
  public services: Service[] = [];

  private filesSub: Subscription;
  private templateSub: Subscription;
  private servicesSub: Subscription;

  public filesLoaded = false;

  private static filterData(file: ConfigFile, query: string): boolean {
    const queryString = query.trim();
    if (Validator.notEmpty(queryString)) {
      return file.name.includes(queryString) || file.targetPath.includes(queryString);
    }
    return true;
  }

  private static filterByTemplate(file: ConfigFile, templateQuery: string) {
    if (Validator.notEmpty(templateQuery)) {
      return file.templates.some(t => t === templateQuery);
    }
    return true;
  }

  private static filterByService(file: ConfigFile, serviceQuery: string) {
    if (Validator.notEmpty(serviceQuery)) {
      return file.dependentServices.some(s => s === serviceQuery);
    }
    return true;
  }

  constructor(private alertService: AlertService,
              private fileHttp: FileHttpService,
              private templateHttp: TemplateHttpService,
              private serviceHttp: ServiceHttpService,
              private router: Router) { }

  ngOnInit(): void {
    this.reloadData();

    this.templateSub = this.templateHttp.getTemplates().subscribe((result) => {
      this.templates = result;
    });

    this.servicesSub = this.serviceHttp.getServices().subscribe((services) => {
      this.services = services;
    });
  }

  ngOnDestroy(): void {
    if (this.filesSub) {
      this.filesSub.unsubscribe();
    }

    if (this.templateSub) {
      this.templateSub.unsubscribe();
    }

    if (this.servicesSub) {
      this.servicesSub.unsubscribe();
    }
  }

  get searchQuery() {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.reloadData();
  }

  get templateQuery() {
    return this._templateQuery;
  }

  set templateQuery(value: string) {
    this._templateQuery = value;
    this.reloadData();
  }

  get serviceQuery() {
    return this._serviceQuery;
  }

  set serviceQuery(value: string) {
    this._serviceQuery = value;
    this.reloadData();
  }

  private reloadData(): void {
    this.filesSub = this.fileHttp.getFiles().subscribe((files) => {
      const filteredFiles = files.filter(f => FileOverviewComponent.filterData(f, this._searchQuery))
                                  .filter(f => FileOverviewComponent.filterByTemplate(f, this._templateQuery))
                                  .filter(f => FileOverviewComponent.filterByService(f, this._serviceQuery))
                                  .sort(Sorter.files);
      this._filesSub.next(filteredFiles);
      this.filesLoaded = true;
    }, (err) => {
      this.alertService.danger('Error loading files and directories!');
      this.filesLoaded = true;
    });
  }

  public gotoDetails(file: ConfigFile) {
    this.router.navigate(['/files', file.name]);
  }

  public deleteFile(fileToDelete: ConfigFile) {
    this.fileHttp.deleteFile(fileToDelete.name).subscribe(() => {
      this.alertService.success(`File '${fileToDelete.name}' successfully deleted!`);
      this.reloadData();
    },
    (err) => {
      console.error(err);
    });
  }

}
