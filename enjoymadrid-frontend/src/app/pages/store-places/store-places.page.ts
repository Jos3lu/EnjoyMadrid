import { Component, OnInit } from '@angular/core';
import { SafeHtml } from '@angular/platform-browser';
import { TouristicPointModel } from 'src/app/models/touristic-point.model';
import { AuthService } from 'src/app/services/auth/auth.service';
import { SharedService } from 'src/app/services/shared/shared.service';
import { StorageService } from 'src/app/services/storage/storage.service';
import { TouristicPointService } from 'src/app/services/touristic-point/touristic-point.service';

@Component({
  selector: 'app-store-places',
  templateUrl: './store-places.page.html',
  styleUrls: ['./store-places.page.scss'],
})
export class StorePlacesPage implements OnInit {

  // Interest points of user
  interestPlaces: TouristicPointModel[];

  constructor(
    private sharedService: SharedService,
    private authService: AuthService,
    private touristicPointsService: TouristicPointService,
    private storageService: StorageService
  ) { }

  ngOnInit() {
  }

  ionViewWillEnter() {
    // Get interest points
    this.interestPlaces = this.sharedService.getTouristicPoints();
  }

  placeSelected(index: number) {
    this.sharedService.placeSelected(this.interestPlaces[index]);
  }

  removePlace(place: TouristicPointModel, index: number) {
    // Remove place from memory
    this.interestPlaces.splice(index, 1);

    // If user logged remove place from DB otherwise remove it from Local Storage
    if (this.authService.isUserLoggedIn()) {
      // Get place & user id, call deleteUserTouristicPoint function
      let userId = this.authService.getUserAuth().id;
      this.touristicPointsService.deleteUserTouristicPoint(userId, place.id).subscribe(
        _ => this.sharedService.showToast('Punto de interés "' + place.name + '" borrado',3000),
        error => this.sharedService.onError(error, 5000)
      );
    } else {
      this.storageService.set('touristicPoints', this.interestPlaces)
        .then(_ => this.sharedService.showToast('Punto de interés "' + place.name + '" borrado',3000))
        .catch(_ => this.sharedService.showToast('No se ha podido borrar el punto de interés', 3000));
    }
  }

  async onError(event: any) {
    // Reload image if error loading it
    this.sharedService.reloadImage(event, 'data-retry', 'data-max-retry', 'assets/flag.png');
  }

  sanitizeHtml(innerHTMl: string): SafeHtml {
    // Sanitize html
    return this.sharedService.sanitizeHtml(innerHTMl);
  }

}
