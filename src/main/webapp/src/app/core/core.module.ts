import { NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { AlertService } from '../util/alert/alert.service';
import { WebSocketService } from '../util/websockets/websocket.service';
import { HostsService } from '../util/hosts/hosts.service';
import { PackageChangesService } from '../util/packagechanges/packagechanges.service';
import { RepoScansService } from '../util/reposcans/reposcans.service';
import { JwtInterceptor } from '../util/http/jwt.interceptor';
import { AuthTokenProviderService } from '../util/auth/authtokenprovider.service';
import { ConfigValueHttpService } from '../util/http/configValue.http.service';
import { RepoMirrorHttpService } from '../util/http/repomirror.http.service';
import { RepoHttpService } from '../util/http/repo.http.service';
import { AdditionalLinkHttpService } from '../util/http/additionalLinks.http.service';
import { ServiceHttpService } from '../util/http/service.http.service';
import { PackageHttpService } from '../util/http/package.http.service';
import { SettingHttpService } from '../util/http/setting.http.service';
import { TemplateHttpService } from '../util/http/template.http.service';
import { HostHttpService } from '../util/http/host.http.service';
import { SSHKeyHttpService } from '../util/http/sshKey.http.service';
import { FileHttpService } from '../util/http/file.http.service';
import { WSConfigHttpService } from '../util/http/wsconfig.http.service';
import { StatsHttpService } from '../util/http/stats.http.service';
import { AuthHttpService } from '../util/http/auth.http.service';
import { UserHttpService } from '../util/http/user.http.service';
import { GroupHttpService } from '../util/http/group.http.service';
import { PermissionHttpService } from '../util/http/permission.http.service';
import { UnauthorizedInterceptor } from '../util/http/unauthorized.interceptor';
import { ServiceUsageHttpService } from '../util/http/serviceUsage.http.service';

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [],
  providers: [
    AlertService,
    AuthTokenProviderService,
    HostsService,
    PackageChangesService,
    RepoScansService,
    WebSocketService,
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: UnauthorizedInterceptor, multi: true },
    AdditionalLinkHttpService,
    AuthHttpService,
    ConfigValueHttpService,
    FileHttpService,
    GroupHttpService,
    HostHttpService,
    PackageHttpService,
    PermissionHttpService,
    RepoHttpService,
    RepoMirrorHttpService,
    ServiceHttpService,
    ServiceUsageHttpService,
    SettingHttpService,
    SSHKeyHttpService,
    StatsHttpService,
    TemplateHttpService,
    UserHttpService,
    WSConfigHttpService
  ]
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error('CoreModule already loaded. Import only in AppModule')
    }
  }
}
