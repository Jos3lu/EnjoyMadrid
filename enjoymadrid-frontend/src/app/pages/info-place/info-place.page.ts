import { Component, Input, OnInit } from '@angular/core';
import { TouristicPointModel } from 'src/app/models/touristic-point.model';
import SwiperCore, { Autoplay, Keyboard, Pagination, Scrollbar, Zoom } from 'swiper';

SwiperCore.use([Autoplay, Pagination, Zoom]);

@Component({
  selector: 'app-info-place',
  templateUrl: './info-place.page.html',
  styleUrls: ['./info-place.page.scss'],
})
export class InfoPlacePage implements OnInit {

  @Input() place: TouristicPointModel;

  constructor() { }

  ngOnInit() {
  }

}
