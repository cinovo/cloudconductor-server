import { Component, OnDestroy, OnInit } from "@angular/core";
import { Router } from "@angular/router";

import { ConfigValueHttpService } from "../util/http/configValue.http.service";
import { AuthorizationGuard } from "../util/auth/authorization.guard";
import { Role } from "../util/enums.util";
import { AlertService } from "../util/alert/alert.service";
import { Subscription } from 'rxjs';

@Component({
  selector: 'cv-list',
  templateUrl: './cv.list.comp.html'
})
export class ConfigValueList implements OnInit, OnDestroy {

  public readonly reservedValues: string[] = ['GLOBAL', 'VARIABLES'];

  private _templates: string[];
  private roleSub: Subscription;
  public mayEdit = false;

  constructor(private readonly confHttp: ConfigValueHttpService,
              private readonly router: Router,
              private readonly authGuard: AuthorizationGuard,
              private readonly alerts: AlertService) {
  }

  ngOnInit(): void {
    this.confHttp.templates.subscribe((templates: string[]) => this.templates = templates);
    this.roleSub = this.authGuard.hasRole(Role.EDIT_CONFIGVALUES).subscribe((mayEdit: boolean) => this.mayEdit = mayEdit);
  }

  ngOnDestroy(): void {
    if (this.roleSub) {
      this.roleSub.unsubscribe();
    }
  }

  public gotoDetails(template: string): void {
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

  public deleteTemplate(template: string): void {
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
