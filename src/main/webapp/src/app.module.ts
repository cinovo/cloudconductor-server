import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import './rxjs-extensions';

import { CoreModule } from './app/core/core.module';
import { SharedModule } from './app/shared/shared.module';

import { AlertComponent } from './app/util/alert/alert.comp';
import { AppComponent } from './app/app.comp';
import { FooterComponent } from './app/footer/footer.comp';
import { NavComponent } from './app/nav/nav.comp';
import { TopNavComponent } from './app/nav/topNav.comp';
import { routing } from './app/app.routing';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch, mweise
 */
@NgModule({
  imports: [
    BrowserModule,
    HttpClientModule,
    routing,
    FormsModule,
    ReactiveFormsModule,
    ConfirmationPopoverModule.forRoot(),
    CoreModule,
    SharedModule,
  ],
  declarations: [
    AppComponent,
    AlertComponent,
    NavComponent,
    TopNavComponent,
    FooterComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
