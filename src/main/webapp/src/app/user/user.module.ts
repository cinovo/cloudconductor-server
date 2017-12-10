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
import { AuthenticationGuard } from '../util/auth/authentication.guard';
import { AuthorizationGuard } from '../util/auth/authorization.guard';

const userRoutes: Routes = [
  {path: '', component: UserOverviewComponent, data: {rolesAllowed: [Role.VIEW_USERS, Role.EDIT_USERS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: 'new', component: UserNewComponent, data: {rolesAllowed: [Role.EDIT_USERS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':loginName', component: UserDetailComponent, data: {rolesAllowed: [Role.EDIT_USERS]},
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
