import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';
import { Subscription } from 'rxjs';
import { UserModel } from './models/user.model';
import { AuthService } from './services/auth/auth.service';
import { EventBusService } from './services/event-bus/event-bus.service';
import { RouteService } from './services/route/route.service';
import { SharedService } from './services/shared/shared.service';
import { StorageService } from './services/storage/storage.service';
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

  // Distance unit for routes
  distanceUnit: string;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private routeService: RouteService,
    private storageService: StorageService,
    private sharedService: SharedService,
    private tokenService: TokenStorageService,
    private route: Router,
    private toastController: ToastController,
    private eventBusService: EventBusService
  ) {
    this.selectDarkOrLightTheme();
    this.setRoutesUser();
    this.eventBusSub = this.eventBusService.on('logout', () => {
      this.signOut();
    });
    this.distanceUnit = 'kilometers';
    this.setDistanceUnit();
  }

  ngOnDestroy() {
    if (this.eventBusSub) {
      this.eventBusSub.unsubscribe();
    }
  }

  async setRoutesUser() {
    if (this.authService.isUserLoggedIn()) {
      let userId = this.authService.getUserAuth().id;
      this.routeService.getUserRoutes(userId).subscribe(
        routes => this.sharedService.setRoutes(routes),
        _ => this.sharedService.setRoutes([])
      );
      this.isUserLogged = true;
    } else {
      await this.storageService.init();
      this.storageService.get('routes').then(routes => {
        if (!routes) {
          routes = [];
          this.storageService.set('routes',routes);
        }
        this.sharedService.setRoutes(routes);
      }).catch(error => {
        console.log(error);
      });
      this.isUserLogged = false;
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
        this.sharedService.showToast('Se ha cerrado la sesión del usuario', 3000);
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
              error => this.sharedService.showToast(error.error?.message, 3000)
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

  setDistanceUnit() {
    // Change the unit distance (km/miles)
    this.sharedService.setDistanceUnit(this.distanceUnit);
  }

}