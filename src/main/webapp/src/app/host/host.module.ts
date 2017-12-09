import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { HostPackages } from './host.package.comp';
import { HostServices } from './host.service.comp';
import { HostOverview } from './host.overview.comp';
import { HostDetail } from './host.detail.comp';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule,

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    HostDetail,
    HostOverview,
    HostPackages,
    HostServices,
  ]
})
export class HostModule { }
