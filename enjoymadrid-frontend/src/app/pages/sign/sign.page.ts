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
        this.tokenService.saveToken(data);
        this.router.navigateByUrl('/');
      }
    );
  }

  onSignUp() {
    console.log(this.userSignUp);
  }

}
