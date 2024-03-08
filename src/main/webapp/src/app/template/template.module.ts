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
import { hasRole } from '../util/auth/authorization.guard';
import { loggedIn } from '../util/auth/authentication.guard';
import { TemplateServiceComponent } from './template.service.comp';
import { TemplateDiff } from "./template.diff.comp";

const templateRoute: Routes = [
  {
    path: '', component: TemplateOverview, title: 'Templates',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_TEMPLATE, Role.EDIT_TEMPLATE])]
  },
  {
    path: 'new', component: TemplateNew, title: 'New template',
    canActivate: [loggedIn(true), hasRole([Role.EDIT_TEMPLATE])]
  },
  {
    path: 'diff', component: TemplateDiff, title: 'Template diff',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_TEMPLATE])]
  },
  {
    path: ':templateName', component: TemplateDetail,
    title: route => `Template ${route.paramMap.get('templateName')}`,
    canActivate: [loggedIn(true), hasRole([Role.VIEW_TEMPLATE, Role.EDIT_TEMPLATE])]
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
