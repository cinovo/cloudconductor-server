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
import { AuthenticationGuard } from '../util/auth/authentication.guard';
import { AuthorizationGuard } from '../util/auth/authorization.guard';

const hostRoutes: Routes = [
  {path: '', component: HostOverview, data: {rolesAllowed: [Role.VIEW_HOST, Role.EDIT_HOST]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':hostUuid', component: HostDetail, data: {rolesAllowed: [Role.VIEW_HOST, Role.EDIT_HOST]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]}
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
