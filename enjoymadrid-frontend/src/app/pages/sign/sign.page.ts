import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserModel } from 'src/app/models/user.model';
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

  // Loading in Sign in and Sign up
  loadingSignIn: boolean;
  loadingSignUp: boolean;

  // Info of the sign in and sign up forms
  userSignIn: UserModel;
  userSignUp: UserModel;

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
    this.loadingSignIn = false;
    this.loadingSignUp = false;
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
    this.loadingSignIn = true;
    this.authService.signIn(this.userSignIn).subscribe(
      _ => {
        this.onResponse();
      },
      _ => {
        this.loadingSignIn = false;
        this.sharedService.showToast('No se ha podido iniciar sesión. Mira que todo sea correcto y vuelve a intentarlo.', 5000);
      }
    );
  }

  onSignUp() {
    this.loadingSignUp = true;
    this.authService.signUp(this.userSignUp).subscribe(
      _ => {
        this.authService.signIn({ username: this.userSignUp.username, password: this.userSignUp.password }).subscribe(
          _ => {
            this.onResponse();
          },
          _ => {
            this.loadingSignUp = false;
            this.sharedService.showToast('Algo ha salido mal en el inicio de sesión', 3000);
          }
        );
      },
      error => {
        this.loadingSignUp = false;
        this.sharedService.handleError(error);
        if (error.error.message) {
          this.sharedService.showToast(error.error.message, 3000);
        }
      }
    );
  }

  onResponse() {
    this.initForms();
    this.router.navigateByUrl('/');
  }

}
