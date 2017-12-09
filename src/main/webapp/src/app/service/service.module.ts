import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { ServiceOverview } from './service.overview.comp';
import { ServiceDetail } from './service.detail.comp';

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
    RouterModule,

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    ServiceDetail,
    ServiceOverview
  ]
})
export class ServiceModule { }
