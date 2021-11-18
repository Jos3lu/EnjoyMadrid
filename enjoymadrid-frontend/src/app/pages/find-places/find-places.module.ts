import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { FindPlacesPageRoutingModule } from './find-places-routing.module';

import { FindPlacesPage } from './find-places.page';
import { InfoPlacePageModule } from '../info-place/info-place.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    FindPlacesPageRoutingModule,
    InfoPlacePageModule
  ],
  declarations: [FindPlacesPage]
})
export class FindPlacesPageModule {}
