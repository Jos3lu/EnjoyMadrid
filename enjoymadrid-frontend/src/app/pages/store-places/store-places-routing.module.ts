import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { StorePlacesPage } from './store-places.page';

const routes: Routes = [
  {
    path: '',
    component: StorePlacesPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class StorePlacesPageRoutingModule {}
