/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { UserMetaDataComponent } from './app/user/user.metadata.comp';
import { GroupDetailComponent } from './app/group/group.detail.comp';
import { UserHttpService } from './app/util/http/user.http.service';
import { GroupOverviewComponent } from './app/group/group.overview.comp';
import { AuthHttpService } from './app/util/http/auth.http.service';
import { HomeServiceStatusComponent } from './app/home/home.servicestatus.comp';
import { PackageChangesService } from './app/util/packagechanges/packagechanges.service';
import { CCDashboardPanel } from './app/util/ccdashboardpanel/ccdashboardpanel.comp';
import { NotFoundComponent } from './app/not-found/not-found.comp';
import { FooterComponent } from './app/footer/footer.comp';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
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
import { CCPagination } from './app/util/ccpagination/ccpagination.comp';
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
import { LoginComponent } from './app/login/login.comp';
import { AuthorizationGuard } from './app/util/auth/authorization.guard';
import { UserOverviewComponent } from './app/user/user.overview.comp';
import { AuthenticationGuard } from './app/util/auth/authentication.guard';
import { AuthTokenProviderService } from './app/util/auth/authtokenprovider.service';
import { ForbiddenComponent } from './app/forbidden/forbidden.comp';
import { JwtInterceptor } from './app/util/http/jwt.interceptor';
import { GroupHttpService } from './app/util/http/group.http.service';
import { UserDetailComponent } from './app/user/user.detail.comp';
import { UserNewComponent } from './app/user/user.new.comp';
import { UserTokenComponent } from './app/user/user.token.comp';
import { GroupMetaDataComponent } from './app/group/group.metadata.comp';
import { CCPanelListComponent } from './app/util/ccpanellist/ccpanellist.comp';
import { PermissionHttpService } from './app/util/http/permission.http.service';
import { GroupNewComponent } from './app/group/group.new.comp';
import { UserAgentsComponent } from './app/user/user.agents.comp';
import { GroupMemberComponent } from './app/group/group.member.comp';

@NgModule({
  imports: [
    BrowserModule,
    Ng2Webstorage,
    RouterModule.forRoot(APP_ROUTES, {useHash: true}),
    HttpClientModule,
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
    CCPagination,
    CCPanelListComponent,
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
    NotFoundComponent,
    HomePackageChangesComponent,
    HomeHostStatusComponent,
    HomeServiceStatusComponent,
    HomeRepoScansComponent,
    HomeStatsComponent,
    LoginComponent,
    UserOverviewComponent,
    UserNewComponent,
    UserMetaDataComponent,
    UserTokenComponent,
    UserAgentsComponent,
    UserDetailComponent,
    GroupOverviewComponent,
    GroupMetaDataComponent,
    GroupNewComponent,
    GroupMemberComponent,
    GroupDetailComponent,
    ForbiddenComponent
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
    PackageChangesService,
    HostsService,
    RepoScansService,
    StatsHttpService,
    AuthHttpService,
    AuthorizationGuard,
    AuthenticationGuard,
    AuthTokenProviderService,
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    UserHttpService,
    GroupHttpService,
    PermissionHttpService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
