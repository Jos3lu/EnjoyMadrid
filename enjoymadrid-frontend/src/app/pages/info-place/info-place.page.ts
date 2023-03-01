import { Component, HostListener, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { SafeHtml } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { ModalController } from '@ionic/angular';
import { PointModel } from 'src/app/models/point.model';
import { TouristicPointModel } from 'src/app/models/touristic-point.model';
import { AuthService } from 'src/app/services/auth/auth.service';
import { SharedService } from 'src/app/services/shared/shared.service';
import { StorageService } from 'src/app/services/storage/storage.service';
import { TouristicPointService } from 'src/app/services/touristic-point/touristic-point.service';
import SwiperCore, { Autoplay, Navigation, Pagination, SwiperOptions, EffectCoverflow } from 'swiper';

SwiperCore.use([ Autoplay, Pagination, Navigation, EffectCoverflow]);

@Component({
  selector: 'app-info-place',
  templateUrl: './info-place.page.html',
  styleUrls: ['./info-place.page.scss'],
  encapsulation: ViewEncapsulation.None
})
export class InfoPlacePage implements OnInit {

  // Slider for images
  optionsSlider: SwiperOptions = {
    grabCursor: true,
    autoplay: true,
    spaceBetween: 30,
    effect: 'coverflow',
    navigation: true,
    loop: true,
    pagination: { clickable: true, dynamicBullets: true }
  }

  // Place information
  @Input() place: TouristicPointModel;

  constructor(
    private modalController: ModalController,
    private router: Router,
    private sharedService: SharedService,
    private touristicPointService: TouristicPointService,
    private authService: AuthService,
    private storageService: StorageService
  ) { }

  ngOnInit() {
  }

  @HostListener('window:popstate', ['$event'])
  closeInfoPlace() {
    // Close modal on back button
    this.modalController.dismiss();
  }

  createRoute() {
    // Send point information as destination to create route 
    this.modalController.dismiss();
    const point: PointModel = { 
      latitude: this.place.latitude, 
      longitude: this.place.longitude, 
      name: this.place.name + ', ' + this.place.address + ', ' + this.place.zipcode + ' Madrid'
    };
    this.sharedService.setDestination(point, false);
    this.router.navigateByUrl('/create-route');
  }

  addFavourite() {
    // Check if interest point is already in favourites
    if (this.sharedService.getTouristicPoints().find(e => e.id === this.place.id)) {
      // Show toast notifying place is already added to favourites
      this.sharedService.showToast('Punto de interés ya añadido a favoritos',3000);
      return;
    }
    // Add touristic point
    if (this.authService.isUserLoggedIn()) {
      // Add touristic point for logged in user
      const userId = this.authService.getUserAuth().id;
      const touristPointId = this.place.id;
      this.touristicPointService.addTouristicPointToUser(userId, touristPointId).subscribe(
        _ => this.onSuccessAddTouristicPointToUser(),
        error => this.sharedService.onError(error, 5000)
      );
    } else {
      // Add touristic point for not logged in user
      this.onSuccessAddTouristicPointToUser();
      // Set touristic points in local storage
      this.storageService.set('touristicPoints', this.sharedService.getTouristicPoints());
    }
  }

  onSuccessAddTouristicPointToUser() {
    // Store interest point information in list of user's touristic points
    this.sharedService.getTouristicPoints().push(this.place);
    // Show toast notifying place is added to favourites
    this.sharedService.showToast('Añadido a favoritos',3000);
  }

  sanitizeHtml(innerHTMl: string): SafeHtml {
    // Sanitize html
    return this.sharedService.sanitizeHtml(innerHTMl);
  }

  async onError(event: any) {
    // Reload image if error loading it
    this.sharedService.reloadImage(event, 'data-retry', 'data-max-retry', 'assets/imageNotFound.png');
  }

}
