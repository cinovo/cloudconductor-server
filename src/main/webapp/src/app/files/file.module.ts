import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

import { SharedModule } from '../shared/shared.module';
import { FileDetailComponent } from './file.detail.comp';
import { FileOverviewComponent } from './file.overview.comp';
import { fileResolver } from './file.resolve';
import { Role } from '../util/enums.util';
import { hasRole } from '../util/auth/authorization.guard';
import { loggedIn } from '../util/auth/authentication.guard';

const fileRoutes: Routes = [
  {
    path: '', component: FileOverviewComponent, title: 'Files',
    canActivate: [loggedIn(true), hasRole([Role.VIEW_CONFIGURATIONS, Role.EDIT_CONFIGURATIONS])]
  },
  {
    path: 'new', component: FileDetailComponent, title: 'New file',
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGURATIONS])]
  },
  {
    path: ':fileName', component: FileDetailComponent,
    title: r => `File ${r.paramMap.get('fileName')}`,
    resolve: {fileForm: fileResolver},
    canActivate: [loggedIn(true), hasRole([Role.EDIT_CONFIGURATIONS])]
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
    RouterModule.forChild(fileRoutes),

    ConfirmationPopoverModule,

    SharedModule
  ],
  declarations: [
    FileDetailComponent,
    FileOverviewComponent,
  ],
})
export class FileModule { }
