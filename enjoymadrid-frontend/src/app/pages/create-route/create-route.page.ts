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

  // Route Model
  route: RouteModel;
  // Preferences of the user
  preferences: any[];
  // Modes of transport that will be used in the route
  transports: any[];
  // Max distance to walk 
  maxDistance: number;
  // Origin point is empty
  originEmpty: boolean 
  // Destination point is empty
  destinationEmpty: boolean;

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
      preferences: {},
      maxDistance: 1,
      origin: {name: '', latitude: 0, longitude: 0},
      destination: {name: '', latitude: 0, longitude: 0},
      transports: []
    };

    this.originEmpty = true;

    if (this.sharedService.isDestinationEmpty()) {
      this.destinationEmpty = true;
    } else {
      this.destinationEmpty = false;
      this.route.destination = this.sharedService.getDestination();
      this.sharedService.setDestination(undefined, true);
    }

    this.maxDistance = 1000;

    this.preferences = [
      { name: 'Cultura y arte', value: 0 }, 
      { name: 'Parques y jardines', value: 0 }, 
      { name: 'Escuelas de cocina y catas de vinos y aceites', value: 0 },
      { name: 'Empresas de guías Turísticas', value: 0 },
      { name: 'Edificios y monumentos', value : 0 },
      { name: 'Centros de ocio', value: 0 },
      { name: 'Deporte', value: 0 },
      { name: 'Tiendas', value: 0 },
      { name: 'Restauración', value: 0 }
    ];

    this.transports =[
      { mode: 'Metro', isChecked: true },
      { mode: 'Bus', isChecked: true },
      { mode: 'Cercanías', isChecked: true },
      { mode: 'BiciMAD', isChecked: true }
    ];

  }

  onRating(value: number, index: number) {
    this.preferences[index].value = value;
  }

  changeColor(indexStar: number, index: number) {
    return indexStar > this.preferences[index].value ? '#ccc' : '#ffc700';
  }

  async selectOrigin() {
    const modal = await this.modalController.create({
      component: SelectPointPage,
      cssClass: 'my-modal',
      componentProps: {
        'isOrigin': true,
        'pointEmpty': this.originEmpty,
        'point': this.route.origin
      }
    });
    modal.present();

    const location = await modal.onWillDismiss();
    if (!location.data) return;
    this.originEmpty = false;
    this.route.origin = location.data.point;
  }

  async selectDestination() {
    const modal = await this.modalController.create({
      component: SelectPointPage,
      cssClass: 'my-modal',
      componentProps: {
        'isOrigin': false,
        'pointEmpty': this.destinationEmpty,
        'point': this.route.destination
      }
    });
    modal.present();

    const location = await modal.onWillDismiss();
    if (!location.data) return;
    this.destinationEmpty = false;
    this.route.destination = location.data.point;
  }

  async onCreateRoute() {

    // Transform preferences from Star rating to Map
    this.route.preferences = this.preferences.reduce((map, preference) => {
      map[preference.name] = preference.value;
      return map;
    }, {});
    
    // Transform transports from checkbox to list
    this.route.transports = this.transports.reduce((list, transport) => {
      if (transport.isChecked) list.push(transport.mode);
      return list;
    }, []);
    
    // Meters -> Kilometers
    this.route.maxDistance = this.maxDistance / 1000;

    this.routeService.createRoute(this.route).subscribe(
      (points: any) => {
        console.log(points);
      }
    );

  }

}
