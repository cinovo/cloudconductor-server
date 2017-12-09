import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import { Ng2Webstorage } from 'ng2-webstorage';
import './rxjs-extensions';

import { CoreModule } from './app/core/core.module';
import { SharedModule } from './app/shared/shared.module';
import { ConfigValueModule } from './app/configvalues/configvalue.module';
import { FileModule } from './app/files/file.module';
import { ForbiddenModule } from './app/forbidden/forbidden.module';
import { GroupModule } from './app/group/group.module';
import { HomeModule } from './app/home/home.module';
import { HostModule } from './app/host/host.module';
import { LoginModule } from './app/login/login.module';
import { NotFoundModule } from './app/not-found/notfound.module';
import { PackageModule } from './app/packages/package.module';
import { RepoModule } from './app/repo/repo.module';
import { ServiceModule } from './app/service/service.module';
import { SettingsModule } from './app/settings/settings.module';
import { SSHModule } from './app/ssh/ssh.module';
import { TemplateModule } from './app/template/template.module';
import { UserModule } from './app/user/user.module';
import { UserSettingsModule } from './app/usersettings/usersettings.module';

import { AlertComponent } from './app/util/alert/alert.comp';
import { AppComponent } from './app/app.comp';
import { FooterComponent } from './app/footer/footer.comp';
import { NavComponent } from './app/nav/nav.comp';
import { TopNavComponent } from './app/nav/topNav.comp';
import { routing } from './app/app.routing';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch, mweise
 */
@NgModule({
  imports: [
    BrowserModule,
    Ng2Webstorage,
    HttpClientModule,
    routing,
    FormsModule,
    ReactiveFormsModule,
    ConfirmationPopoverModule.forRoot(),
    CoreModule,
    SharedModule,
    ConfigValueModule,
    FileModule,
    ForbiddenModule,
    GroupModule,
    HomeModule,
    HostModule,
    LoginModule,
    NotFoundModule,
    PackageModule,
    RepoModule,
    ServiceModule,
    SettingsModule,
    SSHModule,
    TemplateModule,
    UserModule,
    UserSettingsModule
  ],
  declarations: [
    AppComponent,
    AlertComponent,
    NavComponent,
    TopNavComponent,
    FooterComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
