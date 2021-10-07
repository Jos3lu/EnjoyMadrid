import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { UserCommentsPageRoutingModule } from './user-comments-routing.module';

import { UserCommentsPage } from './user-comments.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    UserCommentsPageRoutingModule
  ],
  declarations: [UserCommentsPage]
})
export class UserCommentsPageModule {}
