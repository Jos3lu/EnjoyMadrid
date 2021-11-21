import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toastController } from '@ionic/core';
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  // Common endpoint
  private API_URL = "http://localhost:8080/api/";

  // Communicate info-place with create-route page
  private destinationEmpty: boolean = true;
  private destination: any;

  constructor() { 
  }

  isDestinationEmpty() {
    return this.destinationEmpty;
  }

  setDestination(destination: any, destinationEmpty: boolean) {
    this.destinationEmpty = destinationEmpty;
    this.destination = destination;
  }

  getDestination() {
    return this.destination;
  }

  getApiUrl() {
    return this.API_URL;
  }

  // Show alerts to user
  async showToast(message: string, timeout: number) {
    const toast = await toastController.create({
      color: 'dark',
      duration: timeout,
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
      'Algo ha pasado. Inténtalo de nuevo');
  }

}
