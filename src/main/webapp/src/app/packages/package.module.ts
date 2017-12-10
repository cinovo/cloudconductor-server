import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { PackageOverview } from './package.overview.comp';
import { PackageDetail } from './package.detail.comp';
import { Role } from '../util/enums.util';
import { AuthenticationGuard } from '../util/auth/authentication.guard';
import { AuthorizationGuard } from '../util/auth/authorization.guard';

const packageRoutes: Routes = [
  {path: '', component: PackageOverview, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':packageName', component: PackageDetail, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
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
    RouterModule.forChild(packageRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    PackageDetail,
    PackageOverview
  ]
})
export class PackageModule { }
