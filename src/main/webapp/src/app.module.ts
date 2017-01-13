/**
 * Created by thoprloph on 12.08.2016.
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
import { PackageServerGroupHttpService } from "./app/services/http/packageservergroup.http.service";
import { PackageServerHttpService } from "./app/services/http/packageserver.http.service";
import { AdditionalLinkHttpService } from "./app/services/http/additionalLinks.http.service";
import { ConfigValueHttpService } from "./app/services/http/configValue.http.service";
import { ServiceHttpService } from "./app/services/http/service.http.service";
import { PackageHttpService } from "./app/services/http/package.http.service";

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
    NavComponent,
    TopNavComponent
  ],
  providers: [
    AlertService,
    ConfigValueHttpService,
    PackageServerHttpService,
    PackageServerGroupHttpService,
    AdditionalLinkHttpService,
    ServiceHttpService,
    PackageHttpService,
    ConfirmOptions,
    {provide: Position, useClass: Positioning}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
