import { Component, HostListener, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { ModalController } from '@ionic/angular';
import { PointModel } from 'src/app/models/point.model';
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
    private sanitizer: DomSanitizer
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

  sanitizeHtml(innerHTMl: string) {
    // Sanitize html
    return this.sanitizer.bypassSecurityTrustHtml(innerHTMl);
  }

  async onError(event: any) {
    // Reload image if error loading it
    this.sharedService.reloadImage(event, 'data-retry', 'data-max-retry', 'assets/imageNotFound.png');
  }

}
