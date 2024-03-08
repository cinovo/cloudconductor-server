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
import { hasRole } from '../util/auth/authorization.guard';
import { loggedIn } from '../util/auth/authentication.guard';

const repoRoutes: Routes = [
  {
    path: '', component: RepoOverview, title: 'Repos',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS])],
  },
  {
    path: 'new', component: RepoEdit, title: 'New repo',
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGURATIONS])],
  },
  {
    path: ':repoName', component: RepoEdit,
    title: route => `Repo ${route.paramMap.get('repoName')}`,
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGURATIONS])],
  },
  {
    path: ':repoName/mirror/new', component: MirrorEdit, title: 'New mirror',
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGURATIONS])],
  },
  {
    path: ':repoName/mirror/:mirrorid', component: MirrorEdit,
    title: route => `Mirror ${route.paramMap.get('mirrorid')}`,
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGURATIONS])],
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
