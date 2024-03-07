import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { SharedModule } from '../shared/shared.module';
import { NotFoundComponent } from './not-found.comp';

const notFoundRoutes: Routes = [
  {path: ':type/:name', component: NotFoundComponent, title: 'Not found'},
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
    RouterModule.forChild(notFoundRoutes),

    SharedModule
  ],
  declarations: [
    NotFoundComponent
  ]
})
export class NotFoundModule { }
