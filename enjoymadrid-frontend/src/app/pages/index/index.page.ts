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

  // Momento de obtener las rutas comprobar que el token del usuario no ha caducado

  constructor(
    private sharedService: SharedService
  ) { }

  ngOnInit() {
  }

  ionViewWillEnter() {
    this.routes = this.sharedService.getRoutes();
  }

  routeSelected() {

  }

}
