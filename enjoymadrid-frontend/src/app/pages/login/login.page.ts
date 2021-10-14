import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  // To show/hide the password in login and sign in
  showPasswordLogin: boolean = false;
  showPasswordSignIn: boolean = false;

  userLogin: User;
  userSignIn: User;

  constructor() { }

  ngOnInit() {
    this.userLogin = {email: '', password: ''};
    this.userSignIn = {name: '', email: '', password: ''};
  }

  togglePasswordLogin() {
    this.showPasswordLogin = !this.showPasswordLogin;
  }

  togglePasswordSignIn() {
    this.showPasswordSignIn = !this.showPasswordSignIn;
  }

  onLogin() {
    console.log(this.userLogin);
  }

  onSignIn() {
    console.log(this.userSignIn);
  }

}
