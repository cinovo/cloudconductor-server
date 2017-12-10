import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { SSHOverviewComponent } from './ssh.overview.comp';
import { SSHDetailComponent } from './ssh.detail.comp';
import { SSHEditComponent } from './ssh.edit.comp';
import { Role } from '../util/enums.util';
import { AuthenticationGuard } from '../util/auth/authentication.guard';
import { AuthorizationGuard } from '../util/auth/authorization.guard';

const sshRoutes: Routes = [
  {path: '', component: SSHOverviewComponent, data: {rolesAllowed: [Role.VIEW_SSH, Role.EDIT_SSH]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':owner', component: SSHDetailComponent, data: {rolesAllowed: [Role.EDIT_SSH]},
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
    ReactiveFormsModule,
    RouterModule.forChild(sshRoutes),

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
