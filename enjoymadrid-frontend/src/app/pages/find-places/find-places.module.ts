import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { FindPlacesPageRoutingModule } from './find-places-routing.module';

import { FindPlacesPage } from './find-places.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    FindPlacesPageRoutingModule
  ],
  declarations: [FindPlacesPage]
})
export class FindPlacesPageModule {}
