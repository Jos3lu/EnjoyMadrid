import { Component, OnInit } from '@angular/core';
import { User } from './models/user.model';
import { AuthService } from './services/auth/auth.service';
import { TokenStorageService } from './services/token/token-storage.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {
  
  // Bound to the value of the dark mode toggle
  darkTheme: boolean;
  // #527c9e Color Logo

  userLogged: User;

  constructor(
    private authService: AuthService,
    private tokenService: TokenStorageService
  ) {
    this.selectDarkOrLightTheme();
  }

  isUserLogged() {
    if (this.authService.isUserLoggedIn()) {
      this.userLogged = this.authService.getUserAuth();
    }
  }

  signOut() {
    this.authService.signOut().subscribe(
      _ => {
        this.userLogged = null;
        this.authService.setUserAuth(null);
        this.tokenService.setToken(null);
      }
    );
  }
  
  selectDarkOrLightTheme() {
    // Get preference of user about color scheme
    const prefersDarkTheme = window.matchMedia('(prefers-color-scheme: dark)');
    this.darkTheme = prefersDarkTheme.matches;
    this.toggleTheme();
    // Listen for changes to the prefers-color-scheme media query
    prefersDarkTheme.addEventListener(
      'change',
      mediaQuery => {
        this.darkTheme = mediaQuery.matches;
        this.toggleTheme();
      }
    );
  }

  updateTheme() {
    // Called when dark mode toggle is changed
    this.darkTheme = !this.darkTheme
    this.toggleTheme();
  }

  toggleTheme() {
    // Toggle the dark class on the <body>
    document.body.classList.toggle('dark', this.darkTheme);
  }

}