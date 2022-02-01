import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DisplayRoutePage } from './display-route.page';

const routes: Routes = [
  {
    path: '',
    component: DisplayRoutePage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DisplayRoutePageRoutingModule {}
