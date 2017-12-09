import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { UserOverviewComponent } from './user.overview.comp';
import { UserNewComponent } from './user.new.comp';
import { UserMetaDataComponent } from './user.metadata.comp';
import { UserTokenComponent } from './user.token.comp';
import { UserAgentsComponent } from './user.agents.comp';
import { UserDetailComponent } from './user.detail.comp';

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
    UserOverviewComponent,
    UserNewComponent,
    UserMetaDataComponent,
    UserTokenComponent,
    UserAgentsComponent,
    UserDetailComponent,
  ]
})
export class UserModule { }
