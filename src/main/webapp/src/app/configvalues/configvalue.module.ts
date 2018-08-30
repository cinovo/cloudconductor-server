import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfigValueEdit } from './cs.edit.comp';
import { ConfigValueOverview } from './cv.overview.comp';
import { ConfigValuePreview } from './cv.preview.comp';
import { SharedModule } from '../shared/shared.module';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import { AuthenticationGuard } from '../util/auth/authentication.guard';
import { AuthorizationGuard } from '../util/auth/authorization.guard';
import { Role } from '../util/enums.util';
import { ConfigValueNew } from './cs.new.comp';
import { ConfigValueExport } from './cv.export.comp';
import { ConfigValueList } from "./cv.list.comp";
import { ConfigValueVariables } from "./cv.variables.comp";
import { CvVarsComp } from "./cv.vars.comp";
import { ConfigValueDiff } from "./cv.diff.comp";

const cvRoutes: Routes = [
  {
    path: '', component: ConfigValueList, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: 'diff', component: ConfigValueDiff, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: 'VARIABLES', component: ConfigValueVariables, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: 'preview', component: ConfigValuePreview, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: 'preview/:template', component: ConfigValuePreview, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: ':template', component: ConfigValueOverview, data: {rolesAllowed: [Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: ':template/:service/newTemplate', component: ConfigValueNew, data: {rolesAllowed: [Role.EDIT_CONFIGVALUES]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: ':template/:service/new', component: ConfigValueEdit, data: {rolesAllowed: [Role.EDIT_CONFIGVALUES]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: ':template/export', component: ConfigValueExport, data: {rolesAllowed: [Role.EDIT_CONFIGVALUES]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
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
    RouterModule.forChild(cvRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    ConfigValueEdit,
    ConfigValueOverview,
    ConfigValuePreview,
    ConfigValueNew,
    ConfigValueExport,
    ConfigValueList,
    ConfigValueVariables,
    CvVarsComp,
    ConfigValueDiff
  ]
})
export class ConfigValueModule {}
