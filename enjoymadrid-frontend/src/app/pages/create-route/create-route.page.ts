import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ModalController } from '@ionic/angular';
import { RouteResultModel } from 'src/app/models/route-result.model';
import { RouteModel } from 'src/app/models/route.model';
import { AuthService } from 'src/app/services/auth/auth.service';
import { RouteService } from 'src/app/services/route/route.service';
import { SharedService } from 'src/app/services/shared/shared.service';
import { StorageService } from 'src/app/services/storage/storage.service';
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
    private authService: AuthService,
    private storageService: StorageService,
    private sharedService: SharedService,
    private routeService: RouteService,
    private modalController: ModalController,
    private router: Router
  ) { }

  ngOnInit() {
    this.initRoute();
  }

  ionViewWillLeave() {
    this.initRoute();
  }

  initRoute() {

    this.route = { 
      name: '',
      preferences: {},
      maxDistance: 1,
      origin: { name: '', latitude: 0, longitude: 0 },
      destination: { name: '', latitude: 0, longitude: 0 },
      transports: [],
      date: ''
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

    // Get actual date
    const date = new Date();
    this.route.date = date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear();

    if (this.authService.isUserLoggedIn()) {
      const userId = this.authService.getUserAuth().id;
      this.routeService.createRouteUserLoggedIn(this.route, userId).subscribe(
        (route: RouteResultModel) => {
          // Set id to route from DB
          this.route.id = route.id;
          // Set route
          this.onSuccessCreateRoute(route);
          // Display route
          this.router.navigate(['/display-route']);
        },
        error => this.onErrorCreateRoute(error)
      );
    } else {
      this.routeService.createRouteUserNotLoggedIn(this.route).subscribe(
        (route: RouteResultModel) => {
          // Set route
          this.onSuccessCreateRoute(route);
          // set routes in local storage
          this.storageService.set('routes', this.sharedService.getRoutes());
          // Display route
          this.router.navigate(['/display-route']);
        },
        error => this.onErrorCreateRoute(error)
      );
    }
  }

  onSuccessCreateRoute(route: RouteResultModel) {
    // Store route response to be used in display route page
    this.sharedService.setRoute(route);
    // Store route information in list of user's routes
    this.sharedService.getRoutes().push(this.route);
    // Hide spinner
    this.loadingRoute = false;
  }

  onErrorCreateRoute(error: HttpErrorResponse) {
    this.sharedService.onError(error, 5000);
    // Hide spinner
    this.loadingRoute = false;
  }

}
