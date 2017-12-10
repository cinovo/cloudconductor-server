import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { FileDetailComponent } from './file.detail.comp';
import { FileOverviewComponent } from './file.overview.comp';
import { FileResolver } from './file.resolve';
import { Role } from '../util/enums.util';
import { AuthorizationGuard } from '../util/auth/authorization.guard';
import { AuthenticationGuard } from '../util/auth/authentication.guard';

const fileRoutes: Routes = [
  {path: '', component: FileOverviewComponent, data: {rolesAllowed: [Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: 'new', component: FileDetailComponent, data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
  canActivate: [AuthenticationGuard, AuthorizationGuard]},
  {path: ':fileName', component: FileDetailComponent, resolve: { fileForm: FileResolver },
  data: {rolesAllowed: [Role.EDIT_CONFIGURATIONS]},
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
    RouterModule.forChild(fileRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    FileDetailComponent,
    FileOverviewComponent,
  ], providers: [
    FileResolver
  ]
})
export class FileModule { }
