import { Component, OnInit } from '@angular/core';
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

  // Momento de obtener las rutas comprobar que el token del usuario no ha caducado

  constructor(
    private sharedService: SharedService,
    private storageService: StorageService,
    private authService: AuthService,
    private routeService: RouteService
  ) { }

  ngOnInit() {
  }

  ionViewWillEnter() {
    this.routes = this.sharedService.getRoutes();
    this.selectImage(this.routes.length);
  }

  routeSelected() {

  }

  removeRoute(index: number) {
    // Remove route from memory
    let route: RouteModel[] = this.routes.splice(index, 1);

    // If user logged remove route from DB otherwise remove it from Local Storage
    if (this.authService.isUserLoggedIn()) {
      // Get route & user id, call deleteRoute function
      let userId = this.authService.getUserAuth().id;
      this.routeService.deleteRoute(userId, route[0].id).subscribe(
        _ => this.sharedService.showToast('Ruta "' + route[0].name + '" borrada',3000),
        _ => this.sharedService.showToast('No se ha podido borrar la ruta', 3000)
      );
    } else {
      this.storageService.set('routes', this.routes)
        .then(_ => this.sharedService.showToast('Ruta "' + route[0].name + '" borrada',3000))
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
