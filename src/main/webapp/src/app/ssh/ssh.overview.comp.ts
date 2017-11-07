import { Router } from '@angular/router';
import { Component, OnDestroy, OnInit } from '@angular/core';

import { Observable, Subscription } from 'rxjs/Rx';

import { AlertService } from '../util/alert/alert.service';
import { SSHKeyHttpService } from '../util/http/sshKey.http.service';
import { Template, TemplateHttpService } from '../util/http/template.http.service';
import { Validator } from '../util/validator.util';
import { Sorter } from '../util/sorters.util';
import { SSHKey } from '../util/http/sshkey.model';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './ssh.overview.comp.html',
  styleUrls: [ './ssh.overview.comp.scss']
})
export class SSHOverviewComponent implements OnInit, OnDestroy {

  private _sshKeys: SSHKey[] = [];
  private _searchQuery: string = null;
  private _searchTemplateQuery = '';
  private _templatesSub: Subscription;

  public showAddKey = false;
  public keysLoaded = false;

  public newKey: Observable<SSHKey> = Observable.of({ owner: '', username: 'root', key: '', templates: [] });

  public templates$: Observable<Template[]>;
  public templates: Template[];
  public templateNames: Observable<String[]>;

  private static filterSSHKeys(key: SSHKey, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return key.owner.indexOf(query.trim()) >= 0
    }
    return true;
  }

  private static filterTemplateData(key: SSHKey, templateQuery: string) {
    if (Validator.notEmpty(templateQuery)) {
      return key.templates.some(t => t === templateQuery);
    }
    return true;
  }

  constructor(private alertService: AlertService,
              private sshKeyHttp: SSHKeyHttpService,
              private templateHttp: TemplateHttpService,
              private router: Router) { }

  public ngOnInit(): void {
    this.loadData();

    this.templates$ = this.templateHttp.getTemplates();
    this.templateNames = this.templates$.map(ts => ts.map(t => t.name));
    this._templatesSub = this.templates$.subscribe((templates) => {
      this.templates = templates;
    });
  }

  public ngOnDestroy(): void {
    if (this._templatesSub) {
      this._templatesSub.unsubscribe();
    }
  }

  get searchTemplateQuery() {
    return this._searchTemplateQuery;
  }

  set searchTemplateQuery(value: string) {
    this._searchTemplateQuery = value;
    this.loadData();
  }

  private loadData() {
    this.sshKeyHttp.getKeys().subscribe((keys) => {
      this.sshKeys = keys;
      this.keysLoaded = true;
    }, (err) => {
      this.alertService.danger('Error loading SSH keys!');
    });
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadData();
  }

  get sshKeys() {
    return this._sshKeys;
  }

  set sshKeys(value: SSHKey[]) {
    this._sshKeys = value
      .filter(sshKey => SSHOverviewComponent.filterSSHKeys(sshKey, this._searchQuery))
      .filter(sshKey => SSHOverviewComponent.filterTemplateData(sshKey, this._searchTemplateQuery))
      .sort(Sorter.sshKey);
  }

  public gotoDetails(sshKey: SSHKey) {
    this.router.navigate(['/ssh', sshKey.owner]);
  }

  public deleteKey(sshKey: SSHKey) {
    if (sshKey) {
      this.sshKeyHttp.deleteKey(sshKey.owner).subscribe(() => {
        this.loadData();
      },
      (err) => {
        console.error(err);
      });
    }
  }

  public goToAddKey() {
    this.showAddKey = true;
  }

  public addKey(newKey: SSHKey) {
    // first check whether key does already exist
    this.sshKeyHttp.existsKey(newKey.owner).subscribe((exists) => {
      if (exists) {
        this.alertService.danger('SSH Key with same owner does already exist!');
      } else {
        this.createKey(newKey);
      }
    });
  }

  private createKey(newKey: SSHKey) {
    this.sshKeyHttp.updateKey(newKey).subscribe(() => {
      this.alertService.success(`Successfully created new SSH key for '${newKey.owner}'!`);
      this.newKey = Observable.of({ owner: '', username: 'root', key: '', templates: [] });
      this.loadData();
    },
    (err) => {
      this.alertService.danger(`Error saving key for '${newKey.owner}'!`);
      console.error(err);
    });

    this.showAddKey = false;
  }

}
