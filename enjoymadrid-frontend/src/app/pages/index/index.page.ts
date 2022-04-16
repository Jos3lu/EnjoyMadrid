import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RouteResultModel } from 'src/app/models/route-result.model';
import { RouteModel } from 'src/app/models/route.model';
import { AuthService } from 'src/app/services/auth/auth.service';
import { RouteService } from 'src/app/services/route/route.service';
import { SharedService } from 'src/app/services/shared/shared.service';
import { StorageService } from 'src/app/services/storage/storage.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.page.html',
  styleUrls: ['./index.page.scss'],
})
export class IndexPage implements OnInit { 

  // Get routes of user
  routes: RouteModel[];

  // Image generated randomly associated to route
  indexImages: number[];

  // Show spinner while create route
  loadingRoute: boolean;

  // Route to show in modal
  routeModal: RouteModel;
  indexRoute: number;
  // Show/hide modal to show information of route
  openModal: boolean;

  // Momento de obtener las rutas comprobar que el token del usuario no ha caducado

  constructor(
    private sharedService: SharedService,
    private storageService: StorageService,
    private authService: AuthService,
    private routeService: RouteService,
    private router: Router
  ) { }

  ngOnInit() {
  }

  ionViewWillEnter() {
    this.openModal = false;
    this.loadingRoute = false;
    this.routes = this.sharedService.getRoutes();
    this.selectImage(this.routes.length);
  }

  async routeSelected(route: RouteModel, index: number) {
    this.routeModal = route;
    this.indexRoute = index;
    this.openModal = true;
  }

  createRoute() {
    this.loadingRoute = true;
    this.routeService.createRoute(this.routeModal).subscribe(
      (route: RouteResultModel) => {
        // Store route response to be used in display route page
        this.sharedService.setRoute(route);
        this.router.navigate(['/display-route']);
        this.openModal = false;
      },
      error => {
        this.loadingRoute = false;
        if (error.error?.message) {
          this.sharedService.showToast(error.error?.message, 3000);
        } else {
          this.sharedService.showToast('No se ha podido crear la ruta', 3000);
        }
      }
    );
  }

  countStars(stars: number) {
    // To display the stars in preferences of route
    return new Array(stars);
  }

  removeRoute(route: RouteModel, index: number) {
    // Remove route from memory
    this.routes.splice(index, 1);

    // If user logged remove route from DB otherwise remove it from Local Storage
    if (this.authService.isUserLoggedIn()) {
      // Get route & user id, call deleteRoute function
      let userId = this.authService.getUserAuth().id;
      this.routeService.deleteRoute(userId, route.id).subscribe(
        _ => this.sharedService.showToast('Ruta "' + route.name + '" borrada',3000),
        _ => this.sharedService.showToast('No se ha podido borrar la ruta', 3000)
      );
    } else {
      this.storageService.set('routes', this.routes)
        .then(_ => this.sharedService.showToast('Ruta "' + route.name + '" borrada',3000))
        .catch(_ => this.sharedService.showToast('No se ha podido borrar la ruta', 3000));
    }
  }

  selectImage(index: number) {
    // Random image from assets
    this.indexImages = [];
    for (let i = 0; i < index; i++) {
      this.indexImages.push(Math.floor(Math.random() * 15));
    }
  }

}
