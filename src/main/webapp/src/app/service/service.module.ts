import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { ServiceOverview } from './service.overview.comp';
import { ServiceDetail } from './service.detail.comp';
import { Role } from '../util/enums.util';
import { AuthenticationGuard } from '../util/auth/authentication.guard';
import { AuthorizationGuard } from '../util/auth/authorization.guard';

const serviceRoutes: Routes = [
  {path: '', component: ServiceOverview, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: 'new', component: ServiceDetail, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':serviceName', component: ServiceDetail, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
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
    ReactiveFormsModule,
    RouterModule.forChild(serviceRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    ServiceDetail,
    ServiceOverview
  ]
})
export class ServiceModule { }
