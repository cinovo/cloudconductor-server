///<reference path="host/host.overview.comp.ts"/>
import { Routes } from '@angular/router';

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

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export const APP_ROUTES: Routes = [

  {path: 'home', component: HomeComponent},

  {path: 'host', component: HostOverview},
  {path: 'host/:hostName', component: HostDetail},

  {path: 'template', component: TemplateOverview},
  {path: 'template/new', component: TemplateNew},
  {path: 'template/:templateName', component: TemplateDetail},

  {path: 'config/preview', component: ConfigValuePreview},
  {path: 'config/:template', component: ConfigValueOverview},
  {path: 'config/:template/:service/new', component: ConfigValueEdit},
  {path: 'config/:template/:service/:key', component: ConfigValueEdit},

  {path: 'files', component: FileOverviewComponent},
  {path: 'files/new', component: FileDetailComponent},
  {path: 'files/:fileName', component: FileDetailComponent, resolve: { fileForm: FileResolver }},

  {path: 'service', component: ServiceOverview},
  {path: 'service/:serviceName', component: ServiceDetail},
  {path: 'service/new', component: ServiceDetail},

  {path: 'package', component: PackageOverview},
  {path: 'package/:packageName', component: PackageDetail},

  {path: 'repo', component: RepoOverview},
  {path: 'repo/new', component: RepoEdit},
  {path: 'repo/:repoName', component: RepoEdit},
  {path: 'repo/:repoName/mirror/new', component: MirrorEdit},
  {path: 'repo/:repoName/mirror/:mirrorid', component: MirrorEdit},

  {path: 'ssh', component: SSHOverviewComponent },
  {path: 'ssh/:owner', component: SSHDetailComponent },

  {path: 'settings', component: SettingsOverview},

  {path: '**', redirectTo: '/home', pathMatch: 'full'},
];
