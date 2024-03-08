import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { ServiceOverview } from './service.overview.comp';
import { ServiceDetail } from './service.detail.comp';
import { Role } from '../util/enums.util';
import { hasRole } from '../util/auth/authorization.guard';
import { loggedIn } from '../util/auth/authentication.guard';

const serviceRoutes: Routes = [
  {
    path: '', component: ServiceOverview, title: 'Services',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS])]
  },
  {
    path: ':serviceName', component: ServiceDetail,
    title: route => `Service ${route.paramMap.get('serviceName')}`,
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGURATIONS])]
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
