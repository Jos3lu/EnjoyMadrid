import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user.model';
import { AuthService } from 'src/app/services/auth/auth.service';
import { SharedService } from 'src/app/services/shared/shared.service';

@Component({
  selector: 'app-sign',
  templateUrl: './sign.page.html',
  styleUrls: ['./sign.page.scss'],
})
export class SignPage implements OnInit {

  // To show/hide the password in login and sign in
  showPasswordSignIn: boolean;
  showPasswordSignUp: boolean;

  // Info of the sign in and sign up forms
  userSignIn: User;
  userSignUp: User;

  constructor(
    private authService: AuthService, 
    private sharedService: SharedService,
    private router: Router
    ) {}

  ngOnInit() {
    this.initForms();
  }

  initForms() {
    this.showPasswordSignIn = false;
    this.showPasswordSignUp = false;
    this.userSignIn = {username: '', password: ''};
    this.userSignUp = {name: '', username: '', password: ''};
  }

  togglePasswordSignIn() {
    this.showPasswordSignIn = !this.showPasswordSignIn;
  }

  togglePasswordSignUp() {
    this.showPasswordSignUp = !this.showPasswordSignUp;
  }

  onSignIn() {
    this.authService.signIn(this.userSignIn).subscribe(
      _ => {
        this.onResponse();
      },
      _ => this.sharedService.showToast('No se ha podido iniciar sesión. Mira que todo sea correcto y vuelve a intentarlo.')
    );
  }

  onSignUp() {
    this.authService.signUp(this.userSignUp).subscribe(
      _ => {
        this.authService.signIn({ username: this.userSignUp.username, password: this.userSignUp.password }).subscribe(
          _ => {
            this.onResponse();
          }
        );
      },
      error => {
        this.sharedService.handleError(error);
        if (error.error.message) {
          this.sharedService.showToast(error.error.message);
        }
      }
    );
  }

  onResponse() {
    this.initForms();
    this.router.navigateByUrl('/');
  }

}
