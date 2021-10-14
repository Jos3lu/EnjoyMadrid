import { Injectable } from '@angular/core';
import { toastController } from '@ionic/core';

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  constructor() { }

  async showToast(message: string) {
    const toast = await toastController.create({
      color: 'light',
      duration: 2000,
      message: message
    });

    toast.present();
  }

}
