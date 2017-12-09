import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { GroupOverviewComponent } from './group.overview.comp';
import { GroupDetailComponent } from './group.detail.comp';
import { GroupMetaDataComponent } from './group.metadata.comp';
import { GroupNewComponent } from './group.new.comp';
import { GroupMemberComponent } from './group.member.comp';

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
    GroupDetailComponent,
    GroupMemberComponent,
    GroupMetaDataComponent,
    GroupNewComponent,
    GroupOverviewComponent
  ]
})
export class GroupModule { }
