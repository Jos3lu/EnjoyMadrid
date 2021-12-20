import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { CreateRoutePageRoutingModule } from './create-route-routing.module';

import { CreateRoutePage } from './create-route.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    CreateRoutePageRoutingModule
  ],
  declarations: [CreateRoutePage]
})
export class CreateRoutePageModule {}
