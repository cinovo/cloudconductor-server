import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { Authentication, AuthenticationService } from '../util/auth/authentication.service';
import { AlertService } from '../util/alert/alert.service';

@Component({
  templateUrl: './login.comp.html',
})
export class LoginComponent {

  public loginForm: FormGroup;

  constructor(private fb: FormBuilder,
              private authService: AuthenticationService,
              private alertService: AlertService,
              private router: Router) {
    this.loginForm = fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  public login(auth: Authentication) {
    this.authService.login(auth).subscribe(
      (success) => {
        if (success) {
          this.alertService.success('Successfully logged in!');
          this.router.navigate(['/home']);
        } else {
          this.alertService.danger('Authentication failed!');
          this.loginForm.reset();
        }
      },
      (err) => {
        this.alertService.danger('Error during authentication!');
        console.error(err);
      }
    );
  }

}
