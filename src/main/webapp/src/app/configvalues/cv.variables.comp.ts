import { Component, OnInit } from "@angular/core";
import { ConfigValue, ConfigValueHttpService } from "../util/http/configValue.http.service";
import { AlertService } from "../util/alert/alert.service";
import { Sorter } from "../util/sorters.util";

@Component({
  selector: 'cv-variables',
  templateUrl: './cv.variables.comp.html'
})
export class ConfigValueVariables implements OnInit {

  public kvs: Array<ConfigValue> = [];
  public newKV: ConfigValue = null;
  private kvLoaded: boolean = false;

  constructor(private configHttp: ConfigValueHttpService,
              private alerts: AlertService) {
  };

  ngOnInit(): void {
    this.configHttp.getValues("VARIABLES").subscribe((result) => {
      this.kvs = result;
      this.kvLoaded = true;
    }, (err) => {
      this.alerts.danger(`Error loading config values for template 'VARIABLES'!`);
      this.kvLoaded = true;
    });

  }

  addNew() {
    if (this.newKV) {
      this.newKV.template = "VARIABLES";
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
