import { Component, OnInit } from '@angular/core';
import { StorageService } from 'src/app/services/storage/storage.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.page.html',
  styleUrls: ['./index.page.scss'],
})
export class IndexPage implements OnInit { 

  // Momento de obtener las rutas comprobar que el token del usuario no ha caducado

  constructor(
    private storageService: StorageService
  ) { }

  ngOnInit() {
  }

  routeSelected() {

  }

}
