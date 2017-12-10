import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';

import { ConfigValueEdit } from './cs.edit.comp';
import { ConfigValueOverview } from './cv.overview.comp';
import { ConfigValuePreview } from './cv.preview.comp';
import { CoreModule } from '../core/core.module';
import { SharedModule } from '../shared/shared.module';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import { AuthenticationGuard } from '../util/auth/authentication.guard';
import { AuthorizationGuard } from '../util/auth/authorization.guard';
import { Role } from '../util/enums.util';

const cvRoutes: Routes = [
  {path: 'preview', component: ConfigValuePreview, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':template', component: ConfigValueOverview, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':template/:service/new', component: ConfigValueEdit, data: {rolesAllowed: [Role.EDIT_CONFIGVALUES]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':template/:service/:key', component: ConfigValueEdit, data: {rolesAllowed: [Role.EDIT_CONFIGVALUES]},
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
    RouterModule.forChild(cvRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    ConfigValueEdit,
    ConfigValueOverview,
    ConfigValuePreview
  ]
})
export class ConfigValueModule { }
