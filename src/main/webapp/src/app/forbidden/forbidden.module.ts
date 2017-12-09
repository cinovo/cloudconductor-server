import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';

import { ForbiddenComponent } from './forbidden.comp';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@NgModule({
  imports: [
    CommonModule,

    SharedModule
  ],
  declarations: [
    ForbiddenComponent
  ]
})
export class ForbiddenModule { }
