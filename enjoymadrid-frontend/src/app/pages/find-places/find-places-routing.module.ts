import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { FindPlacesPage } from './find-places.page';

const routes: Routes = [
  {
    path: '',
    component: FindPlacesPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FindPlacesPageRoutingModule {}
