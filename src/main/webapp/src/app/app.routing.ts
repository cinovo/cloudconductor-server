import { RouterModule, Routes } from '@angular/router';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch, mweise
 */
const APP_ROUTES: Routes = [
  {path: 'home', loadChildren: () => import('app/home/home.module').then(m => m.HomeModule)},
  {path: 'host', loadChildren: () => import('app/host/host.module').then(m => m.HostModule)},
  {path: 'template', loadChildren: () => import('app/template/template.module').then(m => m.TemplateModule)},
  {path: 'files', loadChildren: () => import('app/files/file.module').then(m => m.FileModule)},
  {path: 'config', loadChildren: () => import('app/configvalues/configvalue.module').then(m => m.ConfigValueModule)},
  {path: 'service', loadChildren: () => import('app/service/service.module').then(m => m.ServiceModule)},
  {path: 'package', loadChildren: () => import('app/packages/package.module').then(m => m.PackageModule)},
  {path: 'repo', loadChildren: () => import('app/repo/repo.module').then(m => m.RepoModule)},
  {path: 'ssh', loadChildren: () => import('app/ssh/ssh.module').then(m => m.SSHModule)},
  {path: 'settings', loadChildren: () => import('app/settings/settings.module').then(m => m.SettingsModule)},
  {path: 'user', loadChildren: () => import('app/user/user.module').then(m => m.UserModule)},
  {path: 'group', loadChildren: () => import('app/group/group.module').then(m => m.GroupModule)},
  {path: 'usersettings', loadChildren: () => import('app/usersettings/usersettings.module').then(m => m.UserSettingsModule)},
  {path: 'not-found', loadChildren: () => import('app/not-found/notfound.module').then(m => m.NotFoundModule)},
  {path: 'forbidden', loadChildren: () => import('app/forbidden/forbidden.module').then(m => m.ForbiddenModule)},
  {path: 'login', loadChildren: () => import('app/login/login.module').then(m => m.LoginModule)},

  {path: '**', redirectTo: '/home', pathMatch: 'full'},
];

export const routing = RouterModule.forRoot(APP_ROUTES, { useHash: true, relativeLinkResolution: 'legacy' });
