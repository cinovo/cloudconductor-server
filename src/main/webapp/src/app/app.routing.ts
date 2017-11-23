///<reference path="host/host.overview.comp.ts"/>
import { GroupOverviewComponent } from './group/group.overview.comp';
import { Routes } from '@angular/router';
import { AuthGuard } from './util/auth/auth.guard';
import { NotFoundComponent } from './not-found/not-found.comp';
import { AuthTokenOverviewComponent } from './authtoken/authtoken.overview.comp';
import { HomeComponent } from './home/home.comp';
import { ConfigValueOverview } from './configvalues/cv.overview.comp';
import { ConfigValueEdit } from './configvalues/cs.edit.comp';
import { ServiceOverview } from './service/service.overview.comp';
import { ServiceDetail } from './service/service.detail.comp';
import { SettingsOverview } from './settings/settings.overview.comp';
import { PackageOverview } from './packages/package.overview.comp';
import { PackageDetail } from './packages/package.detail.comp';
import { RepoOverview } from './repo/repo.overview.comp';
import { RepoEdit } from './repo/repo.edit.comp';
import { MirrorEdit } from './repo/mirror.edit.comp';
import { TemplateOverview } from './template/template.overview.comp';
import { TemplateDetail } from './template/template.detail.comp';
import { TemplateNew } from './template/template.new.comp';
import { HostOverview } from './host/host.overview.comp';
import { HostDetail } from './host/host.detail.comp';
import { ConfigValuePreview } from './configvalues/cv.preview.comp';
import { SSHOverviewComponent } from './ssh/ssh.overview.comp';
import { SSHDetailComponent } from './ssh/ssh.detail.comp';
import { FileOverviewComponent } from './files/file.overview.comp';
import { FileDetailComponent } from './files/file.detail.comp';
import { FileResolver } from './files/file.resolve';
import { Role } from './util/enums.util';
import { UserOverviewComponent } from './user/user.overview.comp';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export const APP_ROUTES: Routes = [

  {path: 'home', component: HomeComponent },

  {path: 'host', component: HostOverview, data: {rolesAllowed: [Role.VIEW_HOST, Role.EDIT_HOST]}, canActivate: [AuthGuard]},
  {path: 'host/:hostName', component: HostDetail, data: {rolesAllowed: [Role.VIEW_HOST, Role.EDIT_HOST]}, canActivate: [AuthGuard]},

  {path: 'template', component: TemplateOverview, data: {rolesAllowed: [Role.VIEW_TEMPLATE, Role.EDIT_TEMPLATE]}, canActivate: [AuthGuard]},
  {path: 'template/new', component: TemplateNew, data: {rolesAllowed: [Role.EDIT_TEMPLATE]}, canActivate: [AuthGuard]},
  {path: 'template/:templateName', component: TemplateDetail, data: {rolesAllowed: [Role.VIEW_TEMPLATE, Role.EDIT_TEMPLATE]},
   canActivate: [AuthGuard]},

  {path: 'config/preview', component: ConfigValuePreview, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
   canActivate: [AuthGuard]},
  {path: 'config/:template', component: ConfigValueOverview, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
   canActivate: [AuthGuard]},
  {path: 'config/:template/:service/new', component: ConfigValueEdit, data: {rolesAllowed: [Role.EDIT_CONFIGVALUES]},
   canActivate: [AuthGuard]},
  {path: 'config/:template/:service/:key', component: ConfigValueEdit, data: {rolesAllowed: [Role.EDIT_CONFIGVALUES]},
   canActivate: [AuthGuard]},

  {path: 'files', component: FileOverviewComponent, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},
  {path: 'files/new', component: FileDetailComponent, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},
  {path: 'files/:fileName', component: FileDetailComponent, resolve: { fileForm: FileResolver },
   data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]}, canActivate: [AuthGuard]},

  {path: 'service', component: ServiceOverview, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},
  {path: 'service/:serviceName', component: ServiceDetail, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},
  {path: 'service/new', component: ServiceDetail, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},

  {path: 'package', component: PackageOverview, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},
  {path: 'package/:packageName', component: PackageDetail, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},

  {path: 'repo', component: RepoOverview, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},
  {path: 'repo/new', component: RepoEdit, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},
  {path: 'repo/:repoName', component: RepoEdit, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},
  {path: 'repo/:repoName/mirror/new', component: MirrorEdit, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},
  {path: 'repo/:repoName/mirror/:mirrorid', component: MirrorEdit, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},

  {path: 'ssh', component: SSHOverviewComponent, data: {rolesAllowed: [Role.VIEW_SSH, Role.EDIT_SSH]},
   canActivate: [AuthGuard]},
  {path: 'ssh/:owner', component: SSHDetailComponent, data: {rolesAllowed: [Role.EDIT_SSH]},
   canActivate: [AuthGuard]},

  {path: 'settings', component: SettingsOverview, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
   canActivate: [AuthGuard]},

  {path: 'user', component: UserOverviewComponent, data: {rolesAllowed: [Role.VIEW_USERS, Role.EDIT_USERS]}},

  {path: 'group', component: GroupOverviewComponent, data: {rolesAllowed: [Role.VIEW_USERS, Role.EDIT_USERS]}},

  // TODO remove me
  {path: 'token', component: AuthTokenOverviewComponent},

  {path: 'not-found/:type/:name', component: NotFoundComponent},

  {path: '**', redirectTo: '/home', pathMatch: 'full'},
];
