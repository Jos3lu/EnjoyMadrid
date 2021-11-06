import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SelectPointPage } from './select-point.page';

const routes: Routes = [
  {
    path: '',
    component: SelectPointPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SelectPointPageRoutingModule {}
