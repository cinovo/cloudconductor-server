import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { RepoOverview } from './repo.overview.comp';
import { RepoEdit } from './repo.edit.comp';
import { MirrorEdit } from './mirror.edit.comp';
import { Role } from '../util/enums.util';
import { AuthenticationGuard } from '../util/auth/authentication.guard';
import { AuthorizationGuard } from '../util/auth/authorization.guard';

const repoRoutes: Routes = [
  {path: '', component: RepoOverview, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: 'new', component: RepoEdit, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':repoName', component: RepoEdit, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':repoName/mirror/new', component: MirrorEdit, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':repoName/mirror/:mirrorid', component: MirrorEdit, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
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
    RouterModule.forChild(repoRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    MirrorEdit,
    RepoEdit,
    RepoOverview
  ]
})
export class RepoModule { }
