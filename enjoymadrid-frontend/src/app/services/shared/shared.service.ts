import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toastController } from '@ionic/core';
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  // Common endpoint
  API_URL = "http://localhost:8080/api/";

  constructor() { }

  // Show alerts to user
  async showToast(message: string) {
    const toast = await toastController.create({
      color: 'dark',
      duration: 2000,
      message: message,
    });

    toast.present();
  }

  // Handle error in services
  handleError(handleError: HttpErrorResponse) {
    if (handleError.status === 0) {
      // A client-side or network error occurred
      console.error('An error occurred:', handleError.error);
    } else {
      // The backend returned an unsuccessful response code.
      console.error(`Backend returned code ${handleError.status}, body was: `, handleError.error);
    }
    // Return an observable with a error message.
    return throwError(
      'Something bad happened; please try again later.');
  }

}
