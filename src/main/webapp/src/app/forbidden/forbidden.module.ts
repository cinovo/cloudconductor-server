import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { SharedModule } from '../shared/shared.module';
import { ForbiddenComponent } from './forbidden.comp';

const forbiddenRoutes: Routes = [
  {path: '', component: ForbiddenComponent, title: 'Forbidden'}
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
    RouterModule.forChild(forbiddenRoutes),

    SharedModule
  ],
  declarations: [
    ForbiddenComponent
  ]
})
export class ForbiddenModule { }
