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
import { hasRole } from '../util/auth/authorization.guard';
import { loggedIn } from '../util/auth/authentication.guard';

const sshRoutes: Routes = [
  {
    path: '', component: SSHOverviewComponent, title: 'SSH keys',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_SSH, Role.EDIT_SSH])],
  },
  {
    path: ':owner', component: SSHDetailComponent,
    title: route => `SSH key ${route.paramMap.get('owner')}`,
    canActivate: [loggedIn(true), hasRole([Role.EDIT_SSH])],
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
