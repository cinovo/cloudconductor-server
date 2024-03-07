import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { PackageOverview } from './package.overview.comp';
import { PackageDetail } from './package.detail.comp';
import { Role } from '../util/enums.util';
import { hasRole } from '../util/auth/authorization.guard';
import { loggedIn } from '../util/auth/authentication.guard';

const packageRoutes: Routes = [
  {
    path: '', component: PackageOverview, title: 'Packages',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS])],
  },
  {
    path: ':packageName', component: PackageDetail,
    title: route => `Package ${route.paramMap.get('packageName')}`,
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS])],
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
