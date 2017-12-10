import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { LinksEdit } from './links.edit.comp';
import { SettingsOverview } from './settings.overview.comp';
import { Role } from '../util/enums.util';
import { AuthorizationGuard } from '../util/auth/authorization.guard';
import { AuthenticationGuard } from '../util/auth/authentication.guard';

const settingsRoutes: Routes = [
  {path: '', component: SettingsOverview, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
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
