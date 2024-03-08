import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfigValueEdit } from './cs.edit.comp';
import { ConfigValueOverview } from './cv.overview.comp';
import { ConfigValuePreview } from './cv.preview.comp';
import { SharedModule } from '../shared/shared.module';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import { loggedIn } from '../util/auth/authentication.guard';
import { hasRole } from '../util/auth/authorization.guard';
import { Role } from '../util/enums.util';
import { ConfigValueNew } from './cs.new.comp';
import { ConfigValueExport } from './cv.export.comp';
import { ConfigValueList } from "./cv.list.comp";
import { ConfigValueVariables } from "./cv.variables.comp";
import { CvVarsComp } from "./cv.vars.comp";
import { ConfigValueDiff } from "./cv.diff.comp";

const cvRoutes: Routes = [
  {
    path: '', component: ConfigValueList, title: 'Config templates',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES])],
  },
  {
    path: 'diff', component: ConfigValueDiff, title: 'Config diff',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES])],
  },
  {
    path: 'VARIABLES', component: ConfigValueVariables, title: 'Config variables',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES])],
  },
  {
    path: 'preview', component: ConfigValuePreview, title: 'Config preview',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES])],
  },
  {
    path: 'preview/:template', component: ConfigValuePreview,
    title: route => `Config preview ${route.paramMap.get('template')}`,
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES])],
  },
  {
    path: ':template', component: ConfigValueOverview,
    title: route => `Config ${route.paramMap.get('template')}`,
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGVALUES, Role.EDIT_CONFIGVALUES])],
  },
  {
    path: ':template/:service/newTemplate', component: ConfigValueNew, title: 'New config template',
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGVALUES])],
  },
  {
    path: ':template/:service/new', component: ConfigValueEdit, title: 'New config',
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGVALUES])],
  },
  {
    path: ':template/export', component: ConfigValueExport,
    title: route => `Export config ${route.paramMap.get('template')}`,
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGVALUES])],
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
