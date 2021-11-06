import { Component, OnInit } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { RouteModel } from 'src/app/models/route.model';
import { RouteService } from 'src/app/services/route/route.service';
import { SharedService } from 'src/app/services/shared/shared.service';
import { SelectPointPage } from '../select-point/select-point.page';

@Component({
  selector: 'app-create-route',
  templateUrl: './create-route.page.html',
  styleUrls: ['./create-route.page.scss'],
})
export class CreateRoutePage implements OnInit {

  route: RouteModel;
  nameRoute: string;
  preferences: any[];

  constructor(
    private sharedService: SharedService,
    private routeService: RouteService,
    private modalController: ModalController
  ) { }

  ngOnInit() {
    this.initRoute();
  }

  initRoute() {

    this.route = { 
      name: '', 
      maxDist: 1000 
    };

    this.preferences = [
      {
        name: 'Cultura y arte', 
        value: 0
      }, 
      {
        name: 'Parques y jardines',
        value: 0
      }, 
      {
        name: 'Escuelas de cocina y catas de vinos',
        value: 0
      },
      {
        name: 'Empresas de guías Turísticas',
        value: 0
      },
      {
        name: 'Edificios y monumentos',
        value : 0
      },
      {
        name: 'Centros de ocio',
        value: 0
      },
      {
        name: 'Deporte',
        value: 0
      },
      {
        name: 'Tiendas',
        value: 0
      },
      {
        name: 'Restauración',
        value: 0
      }
    ];

  }

  onRating(value: number, index: number) {
    this.preferences[index].value = value;
  }

  changeColor(indexStar: number, index: number) {
    return indexStar > this.preferences[index].value ? '#ccc' : '#ffc700';
  }

  async onCreateRoute() {

    let mapPreferences = this.preferences.reduce((map, preference) => {
      map.set(preference.name, preference.value);
      return map;
    }, new Map<string, number>());

    this.route.preferences = mapPreferences;

    const modal = await this.modalController.create({
      component: SelectPointPage,
      cssClass: 'my-modal',
      componentProps: {
        'route': this.route
      }
    });
    return await modal.present();
/*
    this.routeService.createRoute(this.route).subscribe(
      (points: any) => {
        console.log(points);
      }
    );
*/
  }

}
