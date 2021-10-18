import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user.model';
import { AuthService } from 'src/app/services/auth/auth.service';
import { SharedService } from 'src/app/services/shared/shared.service';
import { TokenStorageService } from 'src/app/services/token/token-storage.service';

@Component({
  selector: 'app-sign',
  templateUrl: './sign.page.html',
  styleUrls: ['./sign.page.scss'],
})
export class SignPage implements OnInit {

  // To show/hide the password in login and sign in
  showPasswordSignIn: boolean = false;
  showPasswordSignUp: boolean = false;

  userSignIn: User;
  userSignUp: User;

  constructor(
    private authService: AuthService, 
    private tokenService: TokenStorageService,
    private sharedService: SharedService,
    private router: Router) { }

  ngOnInit() {
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
      data => {
        this.onResponse(data);
      },
      _ => this.sharedService.showToast('No se ha podido iniciar sesión. Vuelve a intentarlo.')
    );
  }

  onSignUp() {
    this.authService.signUp(this.userSignUp).subscribe(
      _ => {
        this.authService.signIn({ username: this.userSignUp.username, password: this.userSignUp.password }).subscribe(
          data => {
            this.onResponse(data);
          }
        );
      },
      _ => this.sharedService.showToast('No se ha podido crear la cuenta. Vuelve a interntarlo')
    );
  }

  onResponse(data: any) {
    this.tokenService.setToken(data.token);
    this.authService.setUserAuth({ id: data.id, name: data.name, username: data.username, photo: data.photo });
    this.router.navigateByUrl('/');
  }

}
