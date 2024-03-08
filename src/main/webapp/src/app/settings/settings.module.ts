import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { LinksEdit } from './links.edit.comp';
import { SettingsOverview } from './settings.overview.comp';
import { Role } from '../util/enums.util';
import { hasRole } from '../util/auth/authorization.guard';
import { loggedIn } from '../util/auth/authentication.guard';

const settingsRoutes: Routes = [
  {
    path: '', component: SettingsOverview, title: 'Settings',
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGURATIONS])]
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
    RouterModule.forChild(settingsRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    SettingsOverview,
    LinksEdit
  ]
})
export class SettingsModule { }
