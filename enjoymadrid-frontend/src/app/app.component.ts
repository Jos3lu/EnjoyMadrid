import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';
import { Subscription } from 'rxjs';
import { UserModel } from './models/user.model';
import { AuthService } from './services/auth/auth.service';
import { EventBusService } from './services/event-bus/event-bus.service';
import { SharedService } from './services/shared/shared.service';
import { TokenStorageService } from './services/token/token-storage.service';
import { UserService } from './services/user/user.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnDestroy {
  
  // Bound to the value of the dark mode toggle
  darkTheme: boolean;
  // #527c9e Color Logo

  // Information of the logged user
  userLogged: UserModel;
  isUserLogged: boolean;

  // Subscription to logout when refresh token is expired
  eventBusSub?: Subscription;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private sharedService: SharedService,
    private tokenService: TokenStorageService,
    private route: Router,
    private toastController: ToastController,
    private eventBusService: EventBusService
  ) {
    this.selectDarkOrLightTheme();
    this.isUserLogged = false;
    this.eventBusSub = this.eventBusService.on('logout', () => {
      this.signOut();
    });
  }

  ngOnDestroy() {
    if (this.eventBusSub) {
      this.eventBusSub.unsubscribe();
    }
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
              error => console.log(error)//this.sharedService.showToast(error.error.message, 3000)
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