import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { LoginPageRoutingModule } from './sign-routing.module';

import { SignPage } from './sign.page';
import { UniqueUsernameValidator } from 'src/app/validators/uniqueUsername.validator';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    LoginPageRoutingModule
  ],
  declarations: [SignPage, UniqueUsernameValidator]
})
export class LoginPageModule {}
