import { ModuleWithProviders } from '@angular/core';
import { RouterModule, CanActivate, Routes } from '@angular/router';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch, mweise
 */
const APP_ROUTES: Routes = [
  {path: 'home', loadChildren: 'app/home/home.module#HomeModule'},
  {path: 'host', loadChildren: 'app/host/host.module#HostModule'},
  {path: 'template', loadChildren: 'app/template/template.module#TemplateModule'},
  {path: 'files', loadChildren: 'app/files/file.module#FileModule'},
  {path: 'config', loadChildren: 'app/configvalues/configvalue.module#ConfigValueModule'},
  {path: 'service', loadChildren: 'app/service/service.module#ServiceModule'},
  {path: 'package', loadChildren: 'app/packages/package.module#PackageModule'},
  {path: 'repo', loadChildren: 'app/repo/repo.module#RepoModule'},
  {path: 'ssh', loadChildren: 'app/ssh/ssh.module#SSHModule'},
  {path: 'settings', loadChildren: 'app/settings/settings.module#SettingsModule'},
  {path: 'user', loadChildren: 'app/user/user.module#UserModule'},
  {path: 'group', loadChildren: 'app/group/group.module#GroupModule'},
  {path: 'usersettings', loadChildren: 'app/usersettings/usersettings.module#UserSettingsModule'},
  {path: 'not-found', loadChildren: 'app/not-found/notfound.module#NotFoundModule'},
  {path: 'forbidden', loadChildren: 'app/forbidden/forbidden.module#ForbiddenModule'},
  {path: 'login', loadChildren: 'app/login/login.module#LoginModule'},

  {path: '**', redirectTo: '/home', pathMatch: 'full'},
];

export const routing: ModuleWithProviders = RouterModule.forRoot(APP_ROUTES, { useHash: true });
