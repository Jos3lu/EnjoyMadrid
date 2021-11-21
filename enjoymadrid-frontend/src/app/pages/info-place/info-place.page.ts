import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { ModalController } from '@ionic/angular';
import { TouristicPointModel } from 'src/app/models/touristic-point.model';
import { SharedService } from 'src/app/services/shared/shared.service';
import SwiperCore, { Autoplay, Navigation, Pagination, SwiperOptions, EffectCoverflow } from 'swiper';

SwiperCore.use([ Autoplay, Pagination, Navigation, EffectCoverflow]);

@Component({
  selector: 'app-info-place',
  templateUrl: './info-place.page.html',
  styleUrls: ['./info-place.page.scss'],
  encapsulation: ViewEncapsulation.None
})
export class InfoPlacePage implements OnInit {

  optionsSlider: SwiperOptions = {
    grabCursor: true,
    autoplay: true,
    spaceBetween: 30,
    effect: 'coverflow',
    navigation: true,
    loop: true,
    pagination: { clickable: true, dynamicBullets: true }
  }

  @Input() place: TouristicPointModel;

  constructor(
    private modalController: ModalController,
    private router: Router,
    private sharedService: SharedService
  ) { }

  ngOnInit() {
  }

  closeInfoPlace() {
    this.modalController.dismiss();
  }

  createRoute() {
    this.modalController.dismiss();
    const point = { 
      latitude: this.place.latitude, 
      longitude: this.place.longitude, 
      location: this.place.address + ', ' + this.place.zipcode + ' Madrid'
    };
    this.sharedService.setDestination(point, false);
    this.router.navigateByUrl('/create-route');
  }

}
