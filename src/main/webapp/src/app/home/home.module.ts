import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { SharedModule } from '../shared/shared.module';
import { HomeServiceStatusComponent } from './home.servicestatus.comp';
import { HomeComponent } from './home.comp';
import { HomePackageChangesComponent } from './home.pkgchanges.comp';
import { HomeHostStatusComponent } from './home.hoststatus.comp';
import { HomeRepoScansComponent } from './home.reposcans.comp';
import { HomeStatsComponent } from './home.stats.comp';
import { loggedIn } from '../util/auth/authentication.guard';

const homeRoutes: Routes = [
  {path: '', component: HomeComponent, title: 'Home', canActivate: [loggedIn(true)]},
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
    RouterModule.forChild(homeRoutes),

    SharedModule
  ],
  declarations: [
    HomeComponent,
    HomeHostStatusComponent,
    HomePackageChangesComponent,
    HomeRepoScansComponent,
    HomeStatsComponent,
    HomeServiceStatusComponent
  ]
})
export class HomeModule { }
