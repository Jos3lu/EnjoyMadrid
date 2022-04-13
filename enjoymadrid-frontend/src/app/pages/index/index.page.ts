import { Component, OnInit } from '@angular/core';
import { RouteModel } from 'src/app/models/route.model';
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
    private storageService: StorageService
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
    this.routes.splice(index, 1);
    this.storageService.set('routes', this.routes);
  }

  selectImage(index: number) {
    // Random image from assets
    this.indexImages = [];
    for (let i = 0; i < index; i++) {
      this.indexImages.push(Math.floor(Math.random() * 15));
    }
  }

}
