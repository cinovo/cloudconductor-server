import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Authentication, AuthenticationService } from '../util/auth/authentication.service';
import { AlertService } from '../util/alert/alert.service';

@Component({
  selector: 'login',
  templateUrl: './login.comp.html',
})
export class LoginComponent {

  public loginForm: FormGroup;

  constructor(private fb: FormBuilder,
              private authService: AuthenticationService,
              private alertService: AlertService) {
    this.loginForm = fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  public login(auth: Authentication) {
    this.authService.login(auth).subscribe(
      (success) => {
        if (success) {
          this.alertService.success('Successfully logged in');
        } else {
          this.alertService.danger('Authentication failed!');
        }
      },
      (err) => {
        this.alertService.danger('Error during authentication!');
        console.error(err);
      }
    );
  }

}
