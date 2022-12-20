import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { StorePlacesPageRoutingModule } from './store-places-routing.module';

import { StorePlacesPage } from './store-places.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    StorePlacesPageRoutingModule
  ],
  declarations: [StorePlacesPage]
})
export class StorePlacesPageModule {}
