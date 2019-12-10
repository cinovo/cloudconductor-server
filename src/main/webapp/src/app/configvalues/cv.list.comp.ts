import { Component, OnDestroy, OnInit } from "@angular/core";
import { Router } from "@angular/router";

import { ConfigValueHttpService } from "../util/http/configValue.http.service";
import { AuthorizationGuard } from "../util/auth/authorization.guard";
import { Role } from "../util/enums.util";
import { AlertService } from "../util/alert/alert.service";

@Component({
  selector: 'cv-list',
  templateUrl: './cv.list.comp.html'
})
export class ConfigValueList implements OnInit, OnDestroy {

  public reservedValues: string[] = ['GLOBAL', 'VARIABLES'];

  private _templates: string[];
  public mayEdit = false;

  constructor(private confHttp: ConfigValueHttpService,
              private router: Router,
              private authGuard: AuthorizationGuard,
              private alerts: AlertService) {
  }

  ngOnDestroy(): void {
  }

  ngOnInit(): void {
    this.confHttp.templates.subscribe((e) => {
      this.templates = e;
    });
    this.authGuard.hasRole(Role.EDIT_CONFIGVALUES).subscribe((e) => this.mayEdit = e);
  }

  protected gotoDetails(template: string): void {
    if (template) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['config', template]);
    }
  }

  get templates(): string[] {
    return this._templates;
  }

  set templates(value: string[]) {
    this._templates = value.filter(i => !this.reservedValues.includes(i));
  }

  protected deleteTemplate(template: string): void {
    this.confHttp.deleteForTemplate(template)
      .subscribe(
        () => {
          this.alerts.success(`All config values for template '${template}' were deleted successfully!`);
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['config']);
        },
        (err) => {
          this.alerts.danger(`Error deleting config values for template '${template}'!`);
          console.error(err);
        }
      );
  }

  public migrateGlobalConfig(): void {
    this.confHttp.migrateGlobalConfig().subscribe(
      () => this.alerts.success("Successfully migrated global configuration."),
      (err) => {
        this.alerts.danger('Error migrating global config!');
        console.error(err);
      }
    );
  }

}
