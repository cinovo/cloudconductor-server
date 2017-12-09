import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { TemplatePackages } from './template.package.comp';
import { TemplateOverview } from './template.overview.comp';
import { TemplateDetail } from './template.detail.comp';
import { TemplateNew } from './template.new.comp';
import { TemplateMetaData } from './template.metadata.comp';
import { TemplateAgentOptions } from './template.agentoption.comp';

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
    RouterModule,

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
  ]
})
export class TemplateModule { }
