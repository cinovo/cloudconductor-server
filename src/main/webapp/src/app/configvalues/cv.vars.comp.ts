import { Component, Input, OnInit } from "@angular/core";

import { ConfigValue, ConfigValueHttpService } from "../util/http/configValue.http.service";
import { AlertService } from "../util/alert/alert.service";
import { Sorter } from "../util/sorters.util";

@Component({
  selector: 'cv-vars',
  templateUrl: './cv.vars.comp.html'
})
export class CvVarsComp implements OnInit {

  @Input() public template: string;

  public kvs: Array<ConfigValue> = [];
  public newKV: ConfigValue = null;
  public kvLoaded: boolean = false;

  public availableVars: string[] = [];
  public title: string;

  constructor(private configHttp: ConfigValueHttpService,
              private alerts: AlertService) {
  };

  ngOnInit(): void {

    if (this.template) {
      this.title = 'Variable overwrites for this template';
      this.configHttp.getVariableValues(this.template).subscribe((result) => {
        this.kvs = result;
        this.configHttp.getVariableValues().subscribe((result) => {
          for (let v of result) {
            if (this.kvs.filter((value) => value.key == v.key).length <= 0) {
              this.availableVars.push(v.key);
            }
          }
          this.kvLoaded = true;
        }, (err) => {
          this.alerts.danger(`Error loading config values for template 'VARIABLES'!`);
          this.kvLoaded = true;
        });
      }, (err) => {
        this.alerts.danger(`Error loading config values for template '${this.template}'!`);
        this.kvLoaded = true;
      });

    } else {
      this.title = 'Global configuration variables';
      this.configHttp.getValues("VARIABLES").subscribe((result) => {
        this.kvs = result;
        this.kvLoaded = true;
      }, (err) => {
        this.alerts.danger(`Error loading config values for template 'VARIABLES'!`);
        this.kvLoaded = true;
      });
    }
  }


  addNew() {
    if (this.newKV) {
      if (this.template) {
        this.newKV.template = this.template;
        this.newKV.service = "VARIABLES";
      } else {
        this.newKV.template = "VARIABLES";
      }
      if (!this.newKV.key.startsWith("${")) {
        this.newKV.key = "${" + this.newKV.key;
      }
      if (!this.newKV.key.endsWith("}")) {
        this.newKV.key = this.newKV.key + "}";
      }
      this.configHttp.save(this.newKV).subscribe((success) => {
          this.alerts.success("Added new variable: " + this.newKV.key);
          this.kvs.push(this.newKV);
          this.kvs.sort(Sorter.configValue);
          this.availableVars = this.availableVars.filter((v) => v != this.newKV.key);
          this.newKV = null;
        },
        (error) => {
          this.alerts.danger("Failed to add new variable: " + this.newKV.key);
        })

    }
  }

  protected doDelete(kv: ConfigValue) {
    this.configHttp.deleteValue(kv).subscribe(
      () => {
        this.kvs = this.kvs.filter((e) => e != kv);
        this.availableVars.push(kv.key);
        this.alerts.success(`Deleted variable '${kv.key}'`);
      },
      (err) => {
        this.alerts.danger(`Error deleting variable '${kv.key}'`);
      }
    );
  }

  protected save(kv: ConfigValue, event: any) {
    const oldVal = kv.value;
    kv.value = event;
    this.configHttp.save(kv).subscribe((success) => {
        this.alerts.success("Modified value for variable : " + kv.key);
      }, (error) => {
        kv.value = oldVal;
        this.alerts.success("Failed to modify value for variable : " + kv.key);
      }
    );
  }
}
