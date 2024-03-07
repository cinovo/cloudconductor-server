import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { UserOverviewComponent } from './user.overview.comp';
import { UserNewComponent } from './user.new.comp';
import { UserMetaDataComponent } from './user.metadata.comp';
import { UserTokenComponent } from './user.token.comp';
import { UserAgentsComponent } from './user.agents.comp';
import { UserDetailComponent } from './user.detail.comp';
import { Role } from '../util/enums.util';
import { hasRole } from '../util/auth/authorization.guard';
import { loggedIn } from '../util/auth/authentication.guard';

const userRoutes: Routes = [
  {
    path: '', component: UserOverviewComponent, title: 'Users',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_USERS, Role.EDIT_USERS])],
  },
  {
    path: 'new', component: UserNewComponent, title: 'New user',
    canActivate: [loggedIn(true), hasRole([Role.EDIT_USERS])],
  },
  {
    path: ':loginName', component: UserDetailComponent,
    title: route => `User ${route.paramMap.get('loginName')}`,
    canActivate: [loggedIn(true), hasRole( [Role.EDIT_USERS])],
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
    RouterModule.forChild(userRoutes),

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
