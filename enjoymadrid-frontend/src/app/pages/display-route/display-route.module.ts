import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { DisplayRoutePageRoutingModule } from './display-route-routing.module';

import { DisplayRoutePage } from './display-route.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    DisplayRoutePageRoutingModule
  ],
  declarations: [DisplayRoutePage]
})
export class DisplayRoutePageModule {}
