import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UserCommentsPage } from './user-comments.page';

const routes: Routes = [
  {
    path: '',
    component: UserCommentsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserCommentsPageRoutingModule {}
