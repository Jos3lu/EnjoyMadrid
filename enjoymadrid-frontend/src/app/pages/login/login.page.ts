import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  // To show/hide the password in login and sign in
  showPasswordLogin: boolean = false;
  showPasswordSingIn: boolean = false;

  constructor() { }

  ngOnInit() {
  }

  togglePasswordLogin() {
    this.showPasswordLogin = !this.showPasswordLogin;
  }

  togglePasswordSignIn() {
    this.showPasswordSingIn = !this.showPasswordSingIn;
  }

}
