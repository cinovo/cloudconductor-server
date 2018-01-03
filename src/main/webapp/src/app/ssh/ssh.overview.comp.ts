import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

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
  templateUrl: './ssh.overview.comp.html'
})
export class SSHOverviewComponent implements OnInit {

  private _sshKeys: SSHKey[] = [];
  private _searchQuery: string = null;
  private _searchTemplateQuery = '';
  private _userQuery = null;
  private _allSelected = false;

  public showAddKey = false;
  public keysLoaded = false;

  public newKey: Observable<SSHKey> = Observable.of({ owner: '', username: 'root', key: '', templates: [] });

  public templates$: Observable<Template[]>;
  public templateNames: Observable<String[]>;

  public addTemplateForm: FormGroup;

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

  private static filterByUser(key: SSHKey, userQuery: string) {
    if (Validator.notEmpty(userQuery)) {
      return key.username.indexOf(userQuery.trim()) >= 0;
    }
    return true;
  }

  constructor(private alertService: AlertService,
              private fb: FormBuilder,
              private sshKeyHttp: SSHKeyHttpService,
              private templateHttp: TemplateHttpService,
              private router: Router) {
    this.addTemplateForm = fb.group({addTemplate: ['', Validators.required]});
  }

  public ngOnInit(): void {
    this.loadData();

    this.templates$ = this.templateHttp.getTemplates();
    this.templateNames = this.templates$.map(ts => ts.map(t => t.name));
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

  get userQuery(): string {
    return this._userQuery;
  }

  set userQuery(value: string) {
    this._userQuery = value;
    this.loadData();
  }

  get sshKeys() {
    return this._sshKeys;
  }

  set sshKeys(value: SSHKey[]) {
    this._sshKeys = value
      .filter(sshKey => SSHOverviewComponent.filterSSHKeys(sshKey, this._searchQuery))
      .filter(sshKey => SSHOverviewComponent.filterTemplateData(sshKey, this._searchTemplateQuery))
      .filter(sshKey => SSHOverviewComponent.filterByUser(sshKey, this._userQuery))
      .sort(Sorter.sshKey);
  }

  get allSelected() {
    return this._allSelected;
  }

  set allSelected(value: boolean) {
    this._allSelected = value;
    this.sshKeys = this.sshKeys.map(k => {
      k.selected = value;
      return k
    });
  }

  get selectedKeys() {
    return this.sshKeys.filter(k => k.selected);
  }

  public isKeySelected(): boolean {
    return this.selectedKeys.length > 0;
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

  public addTemplateToSelectedKeys(formValue: any): void {
    const templateToAdd = formValue.addTemplate;

    const updateOps: Observable<boolean>[] = this.selectedKeys.filter(key => !key.templates.includes(templateToAdd))
      .map(key => {
        const updatedKey = {
          owner: key.owner,
          username: key.username,
          key: key.key,
          templates: [...key.templates, templateToAdd]
        };
        return this.sshKeyHttp.updateKey(updatedKey);
      });

    Observable.forkJoin(updateOps).subscribe(
      () => {
        this.alertService.success(`Successfully added template '${templateToAdd}' to ${updateOps.length} keys.`);
        this.addTemplateForm.reset();
        this.loadData();
      },
      (err) => {
        this.alertService.danger(`Error adding template '${templateToAdd}' to selected keys!`);
        console.error(err);
      },
      () => this.allSelected = false
    );
  }

  public deleteSelectedKeys(): void {
    const deleteOps: Observable<boolean>[] = this.selectedKeys.map(key => this.sshKeyHttp.deleteKey(key.owner));
    Observable.forkJoin(deleteOps).subscribe(
      () => {
        this.alertService.success(`Successfully deleted ${deleteOps.length} keys.`);
        this.loadData();
      },
      (err) => {
        this.alertService.danger('Error deleting selected keys!');
        console.error(err);
      },
      () => this.allSelected = false
    );
  }

}
