import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SwiperModule } from 'swiper/angular';

import { IonicModule } from '@ionic/angular';

import { InfoPlacePageRoutingModule } from './info-place-routing.module';

import { InfoPlacePage } from './info-place.page';

@NgModule({
  imports: [
    SwiperModule,
    CommonModule,
    FormsModule,
    IonicModule,
    InfoPlacePageRoutingModule
  ],
  declarations: [InfoPlacePage]
})
export class InfoPlacePageModule {}
