import { Component, AfterViewInit } from "@angular/core";
import { ConfigValueHttpService, ConfigValue } from "../util/http/configValue.http.service";
import { ActivatedRoute, Router } from "@angular/router";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";
import { AlertService } from "../util/alert/alert.service";

/**
  * Copyright 2017 Cinovo AG<br>
  * <br>
  *
  * @author psigloch
  */

interface ConfigValueTreeNode {
  name: string;
  kvs: Array<ConfigValue>;
  icon: string;
}

@Component({
  selector: 'cv-overview',
  templateUrl: './cv.overview.comp.html'
})
export class ConfigValueOverview implements AfterViewInit {

  private _searchQuery: string = null;
  private template: string;

  private tree: Array<ConfigValueTreeNode> = [];

  constructor(private configHttp: ConfigValueHttpService, private route: ActivatedRoute,
              private router: Router, private alerts: AlertService) {
  };

  ngAfterViewInit(): void {
    this.route.params.subscribe((params) => {
      this.template = params['template'];
      this.loadData();
    });
  }

  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    this.loadData();
  }

  protected deleteCurrentTemplate() {
    for (let index in this.tree) {
      for (let kv of this.tree[index].kvs) {
        this.deleteKey(kv);
      }
    }
    this.alerts.success("The template \"" + this.template + "\" was deleted successfully!");
    this.router.navigate(['config', 'GLOBAL']);
  }

  protected deleteService(name: string) {
    let element: ConfigValueTreeNode;
    for (let nodeIndex in this.tree) {
      if (this.tree[nodeIndex].name == name) {
        element = this.tree[nodeIndex];
      }
    }
    for (let kv of element.kvs) {
      this.deleteKey(kv);
    }
  }

  private deleteKey(kv: ConfigValue): void {
    this.configHttp.deleteValue(kv).subscribe(
      () => {
        let element: ConfigValueTreeNode;
        for (let nodeIndex in this.tree) {
          if (this.tree[nodeIndex].name == kv.service) {
            element = this.tree[nodeIndex];
          }
        }
        for (let i in element.kvs) {
          if (element.kvs[i].key == kv.key) {
            element.kvs.splice(+i, 1);
          }
        }
        if (element.kvs.length < 1) {
          this.tree.splice(this.tree.indexOf(element), 1);
        }
      }
    );
  }

  private generateTree(result: Array<ConfigValue>): void {
    let temp: {[name: string]: Array<ConfigValue>;} = {};
    for (let cf of result) {
      if (ConfigValueOverview.filterData(cf, this._searchQuery)) {
        continue;
      }
      if (!cf.service) {
        cf.service = "";
      }
      if (!temp[cf.service]) {
        temp[cf.service] = [];
      }
      temp[cf.service].push(cf);
    }


    this.tree = [];
    for (let key in temp) {
      temp[key] = temp[key].sort(Sorter.configValue);
      this.tree.push({name: key, kvs: temp[key], icon: key.trim() == '' ? 'fa-institution' : 'fa-flask'});
    }

    this.tree = this.tree.sort(Sorter.nameField);
  }


  private loadData() {
    this.configHttp.getValues(this.template).subscribe((result) => this.generateTree(result));
  }

  protected goToDetail(cv: ConfigValue) {
    if (cv) {
      this.router.navigate(['config', cv.template, cv.service, cv.key]);
    }
  }

  private static filterData(cf: ConfigValue, query: string): boolean {
    if (Validator.notEmpty(query)) {
      for (let field in cf) {
        if (cf[field].indexOf(query.trim()) >= 0) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
