import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { SharedModule } from '../shared/shared.module';
import { UserSettingsComponent } from './usersettings.comp';
import { loggedIn } from '../util/auth/authentication.guard';

const userSettingsRoutes: Routes = [
  {
    path: '', component: UserSettingsComponent, title: 'User settings',
    canActivate: [loggedIn(true)] // no special role needed
  },
]

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
    RouterModule.forChild(userSettingsRoutes),

    SharedModule
  ],
  declarations: [
    UserSettingsComponent
  ]
})
export class UserSettingsModule { }
