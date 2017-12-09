import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { CCDashboardPanel } from '../util/ccdashboardpanel/ccdashboardpanel.comp';
import { CCFilter } from '../util/ccfilter/ccfilter.comp';
import { CCPagination } from '../util/ccpagination/ccpagination.comp';
import { CCPanel } from '../util/ccpanel/ccpanel.comp';
import { CCTitle } from '../util/cctitle/cctitle.comp';
import { CCPaginationInfoComponent } from '../util/ccpaginationinfo/ccpaginationinfo.comp';
import { CCPanelListComponent } from '../util/ccpanellist/ccpanellist.comp';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover/dist/esm/src/confirmationPopover.module';

// TODO move components to shared folder, create alias

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
    RouterModule,

    ConfirmationPopoverModule
  ],
  declarations: [
    CCDashboardPanel,
    CCFilter,
    CCPagination,
    CCPaginationInfoComponent,
    CCPanel,
    CCPanelListComponent,
    CCTitle
  ],
  exports: [
    CommonModule,
    FormsModule ,

    CCDashboardPanel,
    CCFilter,
    CCPagination,
    CCPaginationInfoComponent,
    CCPanel,
    CCPanelListComponent,
    CCTitle
  ]
})
export class SharedModule { }
