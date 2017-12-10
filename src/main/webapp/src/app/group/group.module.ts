import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { GroupOverviewComponent } from './group.overview.comp';
import { GroupDetailComponent } from './group.detail.comp';
import { GroupMetaDataComponent } from './group.metadata.comp';
import { GroupNewComponent } from './group.new.comp';
import { GroupMemberComponent } from './group.member.comp';
import { Role } from '../util/enums.util';
import { AuthorizationGuard } from '../util/auth/authorization.guard';
import { AuthenticationGuard } from '../util/auth/authentication.guard';

const groupRoutes: Routes = [
  {path: '', component: GroupOverviewComponent, data: {rolesAllowed: [Role.VIEW_USERS, Role.EDIT_USERS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: 'new', component: GroupNewComponent, data: {rolesAllowed: [Role.EDIT_USERS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':groupName', component: GroupDetailComponent, data: {rolesAllowed: [Role.EDIT_USERS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]}
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
    RouterModule.forChild(groupRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    GroupDetailComponent,
    GroupMemberComponent,
    GroupMetaDataComponent,
    GroupNewComponent,
    GroupOverviewComponent
  ]
})
export class GroupModule { }
