import { Component, OnInit } from '@angular/core';

import { AlertService } from '../util/alert/alert.service';
import { SSHKey, SSHKeyHttpService } from '../util/http/sshKey.http.service';
import { TemplateHttpService } from '../util/http/template.http.service';

import { Validator } from '../util/validator.util';
import { Sorter } from '../util/sorters.util';

interface TemplateSelection {
  name: string,
  selected: boolean
}

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
export class SSHOverviewComponent implements OnInit {

  private _sshKeys: SSHKey[] = [];
  private _searchQuery: string = null;

  public showAddKey = false;
  public keysLoaded = false;

  public newKey: SSHKey = { owner: '', username: 'root', key: '', templates: [] };

  public templateSelection: TemplateSelection[] = [];

  private static filterSSHKeys(key: SSHKey, query: string): boolean {
    if (Validator.notEmpty(query)) {
      return key.owner.indexOf(query.trim()) >= 0
    }
    return true;
  }

  constructor(private alertService: AlertService,
              private sshKeyHttp: SSHKeyHttpService,
              private templateHttp: TemplateHttpService) { }

  ngOnInit(): void {
    this.loadData();

    this.templateHttp.getTemplates().subscribe((templates) => {
      this.templateSelection = templates.map((template) => { return { name: template.name, selected: false }});
    })
  }

  private loadData() {
    this.sshKeyHttp.getKeys().subscribe((keys) => {
      this.sshKeys = keys;
      this.keysLoaded = true;
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
      .sort(Sorter.sshKey);
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

  public addKey() {
    this.newKey.templates = this.templateSelection.filter((ts) => ts.selected).map((ts) => ts.name);

    this.sshKeyHttp.updateKey(this.newKey).subscribe(() => {
      this.alertService.success(`Successfully created new SSH key for '${this.newKey.owner}'!`)
      this.newKey = { owner: '', username: 'root', key: '', templates: [] };
      this.loadData();
    },
    (err) => {
      console.error(err);
    });

    this.showAddKey = false;
  }

}
