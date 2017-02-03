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
import { AlertService } from "./app/util/alert/alert.service";
import { ROUTED_COMPONENTS, APP_ROUTES } from "./app/app.routing";
import { TopNavComponent } from "./app/nav/topNav.comp";
import { AlertComponent } from "./app/util/alert/alert.comp";
import { RouterModule } from "@angular/router";
import { HttpModule } from "@angular/http";
import "./rxjs-extensions";
import { ReactiveFormsModule, FormsModule } from "@angular/forms";
import { ConfirmOptions, Position, ConfirmModule } from "angular2-bootstrap-confirm";
import { Positioning } from "angular2-bootstrap-confirm/position";
import { AdditionalLinkHttpService } from "./app/util/http/additionalLinks.http.service";
import { ConfigValueHttpService } from "./app/util/http/configValue.http.service";
import { ServiceHttpService } from "./app/util/http/service.http.service";
import { PackageHttpService } from "./app/util/http/package.http.service";
import { SettingHttpService } from "./app/util/http/setting.http.service";
import { LinksEdit } from "./app/links/links.edit.comp";
import { RepoMirrorHttpService } from "./app/util/http/repomirror.http.service";
import { RepoHttpService } from "./app/util/http/repo.http.service";
import { TemplateHttpService } from "./app/util/http/template.http.service";
import { HostHttpService } from "./app/util/http/host.http.service";
import { HostPackages } from "./app/host/host.package.comp";
import { TemplateMetaData } from "./app/template/template.metadata.comp";
import { TemplateAgentOptions } from "./app/template/template.agentoption.comp";
import { TemplatePackages } from "./app/template/template.package.comp";
import { HostServices } from "./app/host/host.service.comp";
import { CCTitle } from "./app/util/cctitle/cctitle.comp";
import { CCPanel } from "./app/util/ccpanel/ccpanel.comp";
import { CCFilter } from "./app/util/ccfilter/ccfilter.comp";

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
    CCPanel,
    CCTitle,
    CCFilter,
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
