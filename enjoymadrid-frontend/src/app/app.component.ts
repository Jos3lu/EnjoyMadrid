import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {
  
  // Bound to the value of the dark mode toggle
  darkTheme: boolean;
  // #527c9e Color Logo

  constructor() {
    this.selectDarkOrLightTheme();
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