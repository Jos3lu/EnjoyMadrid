import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SelectPointPageRoutingModule } from './select-point-routing.module';

import { SelectPointPage } from './select-point.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SelectPointPageRoutingModule
  ],
  declarations: [SelectPointPage]
})
export class SelectPointPageModule {}
