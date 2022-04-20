import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ModalController } from '@ionic/angular';
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
    private router: Router,
    private modalController: ModalController
  ) { }

  ngOnInit() {
  }

  ionViewWillEnter() {
    this.routes = this.sharedService.getRoutes();
    this.selectImage(this.routes.length);
    this.openModal = false;
    this.loadingRoute = false;
  }

  @HostListener('window:popstate', ['$event'])
  dismissModal() {
    // Close modal when back button selected
    this.openModal = false;
  }

  async routeSelected(route: RouteModel, index: number) {
    this.routeModal = route;
    this.indexRoute = index;
    this.openModal = true;
  }

  createRoute() {
    // Show spinner of loading route
    this.loadingRoute = true;
    // Get route response
    if (this.authService.isUserLoggedIn()) {
      // User logged in
      const userId = this.authService.getUserAuth().id;
      this.routeService.createRouteUserLoggedIn(this.routeModal, userId).subscribe(
        (route: RouteResultModel) => this.onSuccessCreateRoute(route),
        error => {
          this.sharedService.onError(error, 5000);
          // Hide spinner
          this.loadingRoute = false;
        }
      );
    } else {
      // User not logged in
      this.routeService.createRouteUserNotLoggedIn(this.routeModal).subscribe(
        (route: RouteResultModel) => this.onSuccessCreateRoute(route),
        error => {
          this.sharedService.onError(error, 5000);
          // Hide spinner
          this.loadingRoute = false;
        }
      );
    }
  }

  onSuccessCreateRoute(route: RouteResultModel) {
    // Hide spinner
    this.loadingRoute = false;
    // Store route response to be used in display route page & close modal with route information
    this.sharedService.setRoute(route);
    // Hide modal with route information
    this.modalController.dismiss();
    this.openModal = false;
    // Display route
    this.router.navigate(['/display-route']);
  }

  closeRoute() {
    // Cancel button, hide modal 
    this.openModal = false;
  }
  
  formatArray(transports: string[]) {
    // Return transports separated by comma
    return transports.join(', ');
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
        error => this.sharedService.onError(error, 5000)
      );
    } else {
      this.storageService.set('routes', this.routes)
        .then(_ => this.sharedService.showToast('Ruta "' + route.name + '" borrada',3000))
        .catch(_ => this.sharedService.showToast('No se ha podido borrar la ruta', 3000));
    }
  }

  selectImage(index: number) {
    // Random image from assets
    this.indexImages = new Array(index);
    for (let i = 0; i < index; i++) {
      this.indexImages[i] = Math.floor(Math.random() * 15);
    }
  }

}
