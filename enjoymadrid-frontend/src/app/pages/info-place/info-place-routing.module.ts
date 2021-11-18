import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { InfoPlacePage } from './info-place.page';

const routes: Routes = [
  {
    path: '',
    component: InfoPlacePage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InfoPlacePageRoutingModule {}
