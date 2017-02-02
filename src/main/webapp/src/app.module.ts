/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { Ng2Webstorage } from "ng2-webstorage";
import { NavComponent } from "./app/nav/nav.comp";
import { AppComponent } from "./app/app.comp";
import { AlertService } from "./app/services/alert/alert.service";
import { ROUTED_COMPONENTS, APP_ROUTES } from "./app/app.routing";
import { TopNavComponent } from "./app/nav/topNav.comp";
import { AlertComponent } from "./app/services/alert/alert.comp";
import { RouterModule } from "@angular/router";
import { HttpModule } from "@angular/http";
import "./rxjs-extensions";
import { CCPanelComponent } from "./app/services/panelhead/panelhead.comp";
import { ReactiveFormsModule, FormsModule } from "@angular/forms";
import { ConfirmOptions, Position, ConfirmModule } from "angular2-bootstrap-confirm";
import { Positioning } from "angular2-bootstrap-confirm/position";
import { AdditionalLinkHttpService } from "./app/services/http/additionalLinks.http.service";
import { ConfigValueHttpService } from "./app/services/http/configValue.http.service";
import { ServiceHttpService } from "./app/services/http/service.http.service";
import { PackageHttpService } from "./app/services/http/package.http.service";
import { SettingHttpService } from "./app/services/http/setting.http.service";
import { LinksEdit } from "./app/links/links.edit.comp";
import { RepoMirrorHttpService } from "./app/services/http/repomirror.http.service";
import { RepoHttpService } from "./app/services/http/repo.http.service";
import { TemplateHttpService } from "./app/services/http/template.http.service";
import { HostHttpService } from "./app/services/http/host.http.service";
import { HostPackages } from "./app/host/host.package.comp";
import { TemplateMetaData } from "./app/template/template.metadata.comp";
import { TemplateAgentOptions } from "./app/template/template.agentoption.comp";
import { TemplatePackages } from "./app/template/template.package.comp";
import { HostServices } from "./app/host/host.service.comp";

@NgModule({
  imports: [
    BrowserModule,
    Ng2Webstorage,
    RouterModule.forRoot(APP_ROUTES, {useHash: true}),
    HttpModule,
    FormsModule,
    ReactiveFormsModule,
    ConfirmModule
  ],
  declarations: [
    AppComponent,
    AlertComponent,
    CCPanelComponent,
    ROUTED_COMPONENTS(),
    LinksEdit,
    TemplateMetaData,
    TemplatePackages,
    TemplateAgentOptions,
    HostServices,
    HostPackages,
    NavComponent,
    TopNavComponent
  ],
  providers: [
    AlertService,
    ConfigValueHttpService,
    RepoMirrorHttpService,
    RepoHttpService,
    AdditionalLinkHttpService,
    ServiceHttpService,
    PackageHttpService,
    SettingHttpService,
    TemplateHttpService,
    HostHttpService,
    ConfirmOptions,
    {provide: Position, useClass: Positioning}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
