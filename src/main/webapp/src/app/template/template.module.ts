import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { TemplatePackages } from './template.package.comp';
import { TemplateOverview } from './template.overview.comp';
import { TemplateDetail } from './template.detail.comp';
import { TemplateNew } from './template.new.comp';
import { TemplateMetaData } from './template.metadata.comp';
import { TemplateAgentOptions } from './template.agentoption.comp';
import { Role } from '../util/enums.util';
import { AuthenticationGuard } from '../util/auth/authentication.guard';
import { AuthorizationGuard } from '../util/auth/authorization.guard';
import { TemplateServiceComponent } from './template.service.comp';
import { TemplateDiff } from "./template.diff.comp";

const templateRoute: Routes = [
  {
    path: '', component: TemplateOverview, data: {rolesAllowed: [Role.VIEW_TEMPLATE, Role.EDIT_TEMPLATE]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: 'new', component: TemplateNew, data: {rolesAllowed: [Role.EDIT_TEMPLATE]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: 'diff', component: TemplateDiff, data: {rolesAllowed: [Role.VIEW_TEMPLATE]},
    canActivate: [AuthenticationGuard, AuthorizationGuard]
  },
  {
    path: ':templateName', component: TemplateDetail, data: {rolesAllowed: [Role.VIEW_TEMPLATE, Role.EDIT_TEMPLATE]},
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
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(templateRoute),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    TemplatePackages,
    TemplateOverview,
    TemplateDetail,
    TemplateNew,
    TemplateMetaData,
    TemplateAgentOptions,
    TemplateServiceComponent,
    TemplateDiff,
  ],
  providers: [
    DatePipe,
  ]
})
export class TemplateModule {}
