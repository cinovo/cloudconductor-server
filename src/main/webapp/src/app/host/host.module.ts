import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { HostPackages } from './host.package.comp';
import { HostServices } from './host.service.comp';
import { HostOverview } from './host.overview.comp';
import { HostDetail } from './host.detail.comp';
import { Role } from '../util/enums.util';
import { hasRole } from '../util/auth/authorization.guard';
import { loggedIn } from '../util/auth/authentication.guard';

const hostRoutes: Routes = [
  {
    path: '', component: HostOverview, title: 'Hosts',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_HOST, Role.EDIT_HOST])],
  },
  {
    path: ':hostUuid', component: HostDetail,
    title: r => `Host ${r.paramMap.get("hostUuid")}`,
    canActivate: [loggedIn(true), hasRole([Role.VIEW_HOST, Role.EDIT_HOST])],
  },
];

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(hostRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    HostDetail,
    HostOverview,
    HostPackages,
    HostServices,
  ]
})
export class HostModule { }
