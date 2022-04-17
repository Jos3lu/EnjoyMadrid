import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RouteModel } from 'src/app/models/route.model';
import { SharedService } from '../shared/shared.service';

const headerOptionsCreateRoute = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

const headerOptionsGetAddress = {
  headers: new HttpHeaders({ 'Accept': 'application/json, application/geo+json, application/gpx+xml' })
}

@Injectable({
  providedIn: 'root'
})
export class RouteService {

  constructor(
    private httpClient: HttpClient,
    private sharedService: SharedService
  ) { }

  getUserRoutes(userId: number): Observable<any> {
    return this.httpClient.get(this.sharedService.getApiUrl() + 'users/' + userId + '/routes').pipe(
      catchError(this.sharedService.handleError)
    );
  }

  createRouteUserNotLoggedIn(route: RouteModel): Observable<any> {
    return this.httpClient.post(this.sharedService.getApiUrl() + 'routes', route, headerOptionsCreateRoute).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  createRouteUserLoggedIn(route: RouteModel, userId: number) {
    return this.httpClient.post(this.sharedService.getApiUrl() + 'users/' + userId + '/routes', route, headerOptionsCreateRoute).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  deleteRoute(userId: number, routeId: number): Observable<any> {
    return this.httpClient.delete(this.sharedService.getApiUrl() + 'users/' + userId + '/routes/' + routeId).pipe(
      catchError(this.sharedService.handleError)
    )
  }

  getAddressFromCoordinates(latitude: number, longitude: number) {
    return this.httpClient.get('https://api.openrouteservice.org/geocode/reverse?' + 
      'api_key=5b3ce3597851110001cf6248079a826553c748d0aed309710623ce33' + 
      '&point.lon=' + longitude + '&point.lat=' + latitude + '&size=1', headerOptionsGetAddress)
  }
  
}
