/**
 * Created by thoprloph on 12.08.2016.
 */
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { Ng2Webstorage } from "ng2-webstorage";
import { NavComponent } from "./app/nav/nav.component";
import { AppComponent } from "./app/app.component";
import { AlertService } from "./app/services/alert/alert.service";
import { ROUTED_COMPONENTS, APP_ROUTES } from "./app/app.routing";
import { TopNavComponent } from "./app/nav/topNav.component";
import { AlertComponent } from "./app/services/alert/alert.component";
import { RouterModule } from "@angular/router";
import { HttpModule } from "@angular/http";
import "./rxjs-extensions";
import { ConfigHttpService } from "./app/services/http/config.http.service";
import { CCPanelComponent } from "./app/services/panelhead/panelhead.component";
import { ReactiveFormsModule, FormsModule } from "@angular/forms";
import { PackageServerHttpService } from "./app/services/http/packageserver.http.service";
import { ConfirmOptions, Position, ConfirmModule } from "angular2-bootstrap-confirm";
import { Positioning } from "angular2-bootstrap-confirm/position";

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
    ConfigHttpService,
    PackageServerHttpService,
    ConfirmOptions,
    {provide: Position, useClass: Positioning}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
