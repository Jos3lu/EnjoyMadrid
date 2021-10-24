import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';
import { UserModel } from './models/user.model';
import { AuthService } from './services/auth/auth.service';
import { SharedService } from './services/shared/shared.service';
import { TokenStorageService } from './services/token/token-storage.service';
import { UserService } from './services/user/user.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {
  
  // Bound to the value of the dark mode toggle
  darkTheme: boolean;
  // #527c9e Color Logo

  userLogged: UserModel;
  isUserLogged: boolean;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private sharedService: SharedService,
    private tokenService: TokenStorageService,
    private route: Router,
    private toastController: ToastController
  ) {
    this.selectDarkOrLightTheme();
    this.isUserLogged = false;
  }

  checkIfUserLogged() {
    if (this.authService.isUserLoggedIn()) {
      this.userLogged = this.authService.getUserAuth();
      this.isUserLogged = true;
    }
  }

  signOut() {
    this.authService.signOut().subscribe(
      _ => {
        this.userLogged = null;
        this.isUserLogged = false;
        this.authService.setUserAuth(null);
        this.tokenService.setToken(null);
        this.route.navigateByUrl('/');
      }
    );
  }

  async deleteAccount() {
    const toast = await this.toastController.create({
      header: 'Eliminar mi cuenta de forma definitiva',
      position: 'top',
      buttons: [
        {
          side: 'end',
          text: 'Aceptar',
          handler: () => {
            this.userService.deleteUser(this.userLogged.id).subscribe(
              _ => {
                this.signOut();
                this.sharedService.showToast('Cuenta borrada con éxito', 3000);
              },
              error => this.sharedService.showToast(error.error.message, 3000)
            );
          }
        }, {
          side: 'end',
          text: 'Cancelar'
        }
      ]
    });
    if (this.isUserLogged) {
      await toast.present();
    }
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