import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { ConfigFile } from '../util/http/file.http.service';
import { ServiceHttpService } from '../util/http/service.http.service';
import { TemplateHttpService } from '../util/http/template.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './file.detail.comp.html'
})
export class FileDetailComponent implements OnInit {

  public file: ConfigFile = {
    name: '', pkg: '', targetPath: '', owner: '', group: '', fileMode: '',
    isTemplate: false, isReloadable: false, isDirectory: false, dependentServices: [], templates: []
  };

  public serviceNames: Observable<string[]>;
  public templateNames: Observable<string[]>;

  constructor(private serviceHttpService: ServiceHttpService,
              private templateHttpService: TemplateHttpService) { }

  ngOnInit(): void {
    this.serviceNames = this.serviceHttpService.getServiceNames();
    this.templateNames = this.templateHttpService.getTemplateNames();
  }

  public createFile(): void {
    // TODO not implemented yet!
  }

}
