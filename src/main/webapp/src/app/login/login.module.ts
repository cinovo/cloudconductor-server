import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { SharedModule } from '../shared/shared.module';
import { LoginComponent } from './login.comp';
import { AuthenticationGuard } from '../util/auth/authentication.guard';

const loginRoutes: Routes = [
  {path: '', component: LoginComponent, data: {loggedIn: false},
  canActivate: [AuthenticationGuard]}
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
    RouterModule.forChild(loginRoutes),

    SharedModule
  ],
  declarations: [
    LoginComponent
  ]
})
export class LoginModule { }
