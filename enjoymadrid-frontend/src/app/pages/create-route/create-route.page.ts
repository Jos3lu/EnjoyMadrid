import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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
  // Submit button is disabled
  disabled: boolean;
  // Show/hide loading route spinner
  loadingRoute: boolean;

  constructor(
    private sharedService: SharedService,
    private routeService: RouteService,
    private modalController: ModalController,
    private router: Router
  ) { }

  ngOnInit() {
    this.initRoute();
  }

  ionViewWillEnter() {
    this.ngOnInit();
  }

  initRoute() {

    this.route = { 
      name: '',
      preferences: {},
      maxDistance: 1,
      origin: { name: '', latitude: 0, longitude: 0 },
      destination: { name: '', latitude: 0, longitude: 0 },
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
      { category: 'C', name: 'Instalaciones culturales', value: 0 }, 
      { category: 'C', name: 'Parques y jardines', value: 0 }, 
      { category: 'C', name: 'Escuelas de cocina y catas de vinos y aceites', value: 0 },
      { category: 'C', name: 'Empresas de guías Turísticas', value: 0 },
      { category: 'C', name: 'Edificios y monumentos', value : 0 },
      { category: 'C', name: 'Centros de ocio', value: 0 },
      { category: 'D', name: 'Deportes', value: 0 },
      { category: 'T', name: 'Tiendas', value: 0 },
      { category: 'R', name: 'Restauración', value: 0 }
    ];

    this.transports = [
      { mode: 'Metro', isChecked: true },
      { mode: 'Bus', isChecked: true },
      { mode: 'Cercanías', isChecked: true },
      { mode: 'BiciMAD', isChecked: true }
    ];

    this.disabled = false;
    this.loadingRoute = false;

  }

  onRating(value: number, index: number) {
    this.preferences[index].value = value;
  }

  changeColor(indexStar: number, index: number) {
    return indexStar > this.preferences[index].value ? '#ccc' : '#ffc700';
  }

  ratingSelected(index: number) {
    return this.preferences[index].value > 0;
  }

  clearRating(index: number) {
    this.preferences[index].value = 0;
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

  onChange(transports: any) {
    for (let transport of transports) {
      if (transport.isChecked) {
        this.disabled = false;
        return;
      }
    }
    this.disabled = true;
  }

  async onCreateRoute() {

    // Show loading spinner while creating route
    this.loadingRoute = true;

    // Transform preferences from Star rating to Map
    this.route.preferences = this.preferences.reduce((map, preference) => {
      map[preference.category + '_' +  preference.name] = preference.value;
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
      (route: any) => {
        this.loadingRoute = false;
        this.sharedService.setRoute(route);
        this.router.navigate(['/display-route']);
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

}
