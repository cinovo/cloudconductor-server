import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { FileDetailComponent } from './file.detail.comp';
import { FileOverviewComponent } from './file.overview.comp';
import { FileResolver } from './file.resolve';

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
    FileDetailComponent,
    FileOverviewComponent,
  ], providers: [
    FileResolver
  ]
})
export class FileModule { }
