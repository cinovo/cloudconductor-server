import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { SSHOverviewComponent } from './ssh.overview.comp';
import { SSHDetailComponent } from './ssh.detail.comp';
import { SSHEditComponent } from './ssh.edit.comp';

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
    SSHDetailComponent,
    SSHEditComponent,
    SSHOverviewComponent
  ]
})
export class SSHModule { }
