/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { HomeServiceStatusComponent } from './app/home/home.servicestatus.comp';
import { PackageChangesService } from './app/util/packagechanges/packagechanges.service';
import { CCDashboardPanel } from './app/util/ccdashboardpanel/ccdashboardpanel.comp';
import { NotFoundComponent } from './app/not-found/not-found.comp';
import { AuthTokenHttpService } from './app/util/http/authtoken.http.service';
import { AuthTokenOverviewComponent } from './app/authtoken/authtoken.overview.comp';
import { FooterComponent } from './app/footer/footer.comp';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { HttpModule } from '@angular/http';
import { Ng2Webstorage } from 'ng2-webstorage';
import { NavComponent } from './app/nav/nav.comp';
import { AppComponent } from './app/app.comp';
import { AlertService } from './app/util/alert/alert.service';
import { APP_ROUTES } from './app/app.routing';
import { TopNavComponent } from './app/nav/topNav.comp';
import { AlertComponent } from './app/util/alert/alert.comp';
import './rxjs-extensions';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import { AdditionalLinkHttpService } from './app/util/http/additionalLinks.http.service';
import { ConfigValueHttpService } from './app/util/http/configValue.http.service';
import { ServiceHttpService } from './app/util/http/service.http.service';
import { PackageHttpService } from './app/util/http/package.http.service';
import { SettingHttpService } from './app/util/http/setting.http.service';
import { LinksEdit } from './app/links/links.edit.comp';
import { RepoMirrorHttpService } from './app/util/http/repomirror.http.service';
import { RepoHttpService } from './app/util/http/repo.http.service';
import { TemplateHttpService } from './app/util/http/template.http.service';
import { HostHttpService } from './app/util/http/host.http.service';
import { HostPackages } from './app/host/host.package.comp';
import { TemplateMetaData } from './app/template/template.metadata.comp';
import { TemplateAgentOptions } from './app/template/template.agentoption.comp';
import { TemplatePackages } from './app/template/template.package.comp';
import { HostServices } from './app/host/host.service.comp';
import { CCTitle } from './app/util/cctitle/cctitle.comp';
import { CCPanel } from './app/util/ccpanel/ccpanel.comp';
import { CCFilter } from './app/util/ccfilter/ccfilter.comp';
import { HomeComponent } from './app/home/home.comp';
import { ConfigValueOverview } from './app/configvalues/cv.overview.comp';
import { ConfigValueEdit } from './app/configvalues/cs.edit.comp';
import { ServiceOverview } from './app/service/service.overview.comp';
import { ServiceDetail } from './app/service/service.detail.comp';
import { SettingsOverview } from './app/settings/settings.overview.comp';
import { PackageOverview } from './app/packages/package.overview.comp';
import { PackageDetail } from './app/packages/package.detail.comp';
import { RepoOverview } from './app/repo/repo.overview.comp';
import { RepoEdit } from './app/repo/repo.edit.comp';
import { MirrorEdit } from './app/repo/mirror.edit.comp';
import { TemplateOverview } from './app/template/template.overview.comp';
import { TemplateDetail } from './app/template/template.detail.comp';
import { TemplateNew } from './app/template/template.new.comp';
import { HostOverview } from './app/host/host.overview.comp';
import { HostDetail } from './app/host/host.detail.comp';
import { ConfigValuePreview } from './app/configvalues/cv.preview.comp';
import { SSHOverviewComponent } from './app/ssh/ssh.overview.comp';
import { SSHKeyHttpService } from './app/util/http/sshKey.http.service';
import { SSHDetailComponent } from './app/ssh/ssh.detail.comp';
import { SSHEditComponent } from './app/ssh/ssh.edit.comp';
import { FileOverviewComponent } from './app/files/file.overview.comp';
import { FileHttpService } from './app/util/http/file.http.service';
import { FileDetailComponent } from './app/files/file.detail.comp';
import { FileResolver } from './app/files/file.resolve';
import { WebSocketService } from './app/util/websockets/websocket.service';
import { WSConfigHttpService } from './app/util/http/wsconfig.http.service';
import { HomePackageChangesComponent } from './app/home/home.pkgchanges.comp';
import { HomeHostStatusComponent } from './app/home/home.hoststatus.comp';
import { HostsService } from './app/util/hosts/hosts.service';
import { HomeRepoScansComponent } from './app/home/home.reposcans.comp';
import { RepoScansService } from './app/util/reposcans/reposcans.service';
import { HomeStatsComponent } from './app/home/home.stats.comp';
import { StatsHttpService } from './app/util/http/stats.http.service';

@NgModule({
  imports: [
    BrowserModule,
    Ng2Webstorage,
    RouterModule.forRoot(APP_ROUTES, {useHash: true}),
    HttpModule,
    FormsModule,
    ReactiveFormsModule,
    ConfirmationPopoverModule.forRoot()
  ],
  declarations: [
    AppComponent,
    AlertComponent,
    CCDashboardPanel,
    CCPanel,
    CCTitle,
    CCFilter,
    HomeComponent,
    ConfigValueOverview,
    ConfigValueEdit,
    ServiceOverview,
    ServiceDetail,
    SettingsOverview,
    PackageOverview,
    PackageDetail,
    RepoOverview,
    RepoEdit,
    MirrorEdit,
    TemplateOverview,
    TemplateDetail,
    TemplateNew,
    HostOverview,
    HostDetail,
    ConfigValuePreview,
    LinksEdit,
    TemplateMetaData,
    TemplatePackages,
    TemplateAgentOptions,
    HostServices,
    HostPackages,
    NavComponent,
    TopNavComponent,
    SSHOverviewComponent,
    SSHDetailComponent,
    SSHEditComponent,
    FileOverviewComponent,
    FileDetailComponent,
    FooterComponent,
    AuthTokenOverviewComponent,
    NotFoundComponent,
    HomePackageChangesComponent,
    HomeHostStatusComponent,
    HomeServiceStatusComponent,
    HomeRepoScansComponent,
    HomeStatsComponent
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
    SSHKeyHttpService,
    FileHttpService,
    FileResolver,
    WSConfigHttpService,
    WebSocketService,
    AuthTokenHttpService,
    PackageChangesService,
    HostsService,
    RepoScansService,
    StatsHttpService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
