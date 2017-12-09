import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { LinksEdit } from './links.edit.comp';
import { SettingsOverview } from './settings.overview.comp';

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

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    SettingsOverview,
    LinksEdit
  ]
})
export class SettingsModule { }
