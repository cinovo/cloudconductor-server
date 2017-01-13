import { Component, AfterViewInit } from "@angular/core";
import { ConfigValueHttpService, ConfigValue } from "../services/http/configValue.http.service";
import { ActivatedRoute } from "@angular/router";
import { Sorter } from "../util/sorters.util";
import { Validator } from "../util/validator.util";

/**
 * Created by psigloch on 10.01.2017.
 */

export interface ConfigValieTreeNode {
  name: string;
  kvs: Array<ConfigValue>;
}

@Component({
  moduleId: module.id,
  selector: 'cv-overview',
  templateUrl: 'html/cv.overview.comp.html'
})
export class ConfigValueOverview implements AfterViewInit {

  private _searchQuery: string = null;
  private template: string;

  private tree: Array<ConfigValieTreeNode> = [];

  constructor(private configHttp: ConfigValueHttpService, private route: ActivatedRoute) {
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

  private deleteCurrentTemplate() {
    for (let index in this.tree) {
      for (let kv of this.tree[index].kvs) {
        if (+index >= this.tree.length - 1) {
          this.deleteKey(kv, () => this.configHttp.reloadTemplates());
        } else {
          this.deleteKey(kv);
        }
      }
    }
  }

  private deleteService(name: string) {
    let element: ConfigValieTreeNode;
    for (let nodeIndex in this.tree) {
      if (this.tree[nodeIndex].name == name) {
        element = this.tree[nodeIndex];
      }
    }
    for (let kv of element.kvs) {
      this.deleteKey(kv);
    }
  }

  private deleteKey(kv: ConfigValue, successCallback?: () => any): void {
    this.configHttp.deleteValue(kv).subscribe(
      () => {
        let element: ConfigValieTreeNode;
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
        if (successCallback) {
          successCallback();
        }
      }
    );
  }

  private generateTree(result: Array<ConfigValue>): void {
    let temp: {[name: string]: Array<ConfigValue>; } = {};
    for (let cf of result) {
      if (this.filterData(cf, this._searchQuery)) {
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
      this.tree.push({name: key, kvs: temp[key]});
    }

    this.tree = this.tree.sort(Sorter.node);
  }



  private loadData() {
    this.configHttp.getValues(this.template).subscribe((result) => this.generateTree(result));
  }

  private filterData(cf: ConfigValue, query: string): boolean {
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
