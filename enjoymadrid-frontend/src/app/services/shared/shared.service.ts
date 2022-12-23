import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ModalController } from '@ionic/angular';
import { toastController } from '@ionic/core';
import { Subject, throwError } from 'rxjs';
import { PointModel } from 'src/app/models/point.model';
import { RouteResultModel } from 'src/app/models/route-result.model';
import { RouteModel } from 'src/app/models/route.model';
import { TouristicPointModel } from 'src/app/models/touristic-point.model';
import { InfoPlacePage } from 'src/app/pages/info-place/info-place.page';
import { EventBusService } from '../event-bus/event-bus.service';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  // Common endpoint
  private API_URL = "http://localhost:8080/api/";

  // Communicate info-place with create-route page (destination point)
  private destinationEmpty: boolean = true;
  private destination: PointModel;

  // Communicate route -> create-route with display-route
  private routeResult: RouteResultModel;

  // Store routes of user in memory
  private routes: RouteModel[];

  // Store touristic/interest points in memory
  private touristicPoints: TouristicPointModel[];

  // Distance unit for routes
  private distanceUnit: string;
  private distanceUnitChange: Subject<string>;

  constructor(
    private eventBusService: EventBusService,
    private modalContrall: ModalController,
    private sanitizer: DomSanitizer
  ) { 
    this.distanceUnitChange = new Subject<string>();
  }

  getRoute(): RouteResultModel {
    return this.routeResult;
  }

  setRoute(routeResult: RouteResultModel) {
    this.routeResult = routeResult;
  }

  getRoutes(): RouteModel[] {
    return this.routes;
  }

  setRoutes(routes: RouteModel[]) {
    this.routes = routes;
  }

  getTouristicPoints() {
    return this.touristicPoints;
  }

  setTouristicPoints(touristicPoints: TouristicPointModel[]) {
    this.touristicPoints = touristicPoints;
  }

  isDestinationEmpty(): boolean {
    return this.destinationEmpty;
  }

  setDestination(destination: PointModel, destinationEmpty: boolean) {
    this.destinationEmpty = destinationEmpty;
    this.destination = destination;
  }

  getDestination(): PointModel {
    return this.destination;
  }

  setDistanceUnit(distanceUnit: string) {
    this.distanceUnit = distanceUnit;
    this.distanceUnitChange.next(distanceUnit);
  }

  getDistanceUnit(): string {
    return this.distanceUnit;
  }

  getDistanceUnitChange(): Subject<string> {
    return this.distanceUnitChange;
  }

  getApiUrl(): string {
    return this.API_URL;
  }

  async placeSelected(place: TouristicPointModel) {
    // Open modal with point information
    const modal = await this.modalContrall.create({
      cssClass: 'my-modal',
      component: InfoPlacePage,
      componentProps: {
        'place': place
      }
    });
    await modal.present();
  }

  sanitizeHtml(innerHTMl: string): SafeHtml {
    // Sanitize html
    return this.sanitizer.bypassSecurityTrustHtml(innerHTMl);
  }

  // Show alerts to user
  async showToast(message: string, timeout: number) {
    if (!message) return;
    const toast = await toastController.create({
      color: 'dark',
      duration: timeout,
      message: message,
    });

    toast.present();
  }

  // Reload image if any error happens
  async reloadImage(event: any, retry: string, maxRetry: string, fallback: string) {
    // Get src of target, get attributes & check retry, try to reload image
    const originalSrc = event.target.src;
    if (parseInt(event.target.getAttribute(retry)) !== parseInt(event.target.getAttribute(maxRetry))) {
      event.target.setAttribute(retry, parseInt(event.target.getAttribute(retry)) + 1);
      event.target.src = fallback;
      setTimeout(function () {
        event.target.src = originalSrc;
      }, 250);
    } else {
      event.target.src = fallback;
    }
  }

  // Handle error in components
  onError(error: HttpErrorResponse, timeout: number) {
    // If response status tells us the access token & refresh token are expired we dispatch logout event to AppComponent
    if (error.status === 403) {
      this.eventBusService.emit({ name: 'logout', value: null });
    }

    // Show message
    this.showToast(error.error?.message, timeout);
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

    // Return the same error.
    return throwError(handleError);
  }

}
