import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { TouristicPointModel } from 'src/app/models/touristic-point.model';
import { SharedService } from '../shared/shared.service';

@Injectable({
  providedIn: 'root'
})
export class TouristicPointService {

  constructor(
    private httpClient: HttpClient, 
    private sharedService: SharedService
  ) { }

  getTouristicPointsByCategory(category: string): Observable<TouristicPointModel[]> {
    return this.httpClient.get<TouristicPointModel[]>(this.sharedService.getApiUrl() + 'tourist-points?category=' + category).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  getTouristicPointsByQuery(query: string): Observable<TouristicPointModel[]> {
    return this.httpClient.get<TouristicPointModel[]>(this.sharedService.getApiUrl() + 'tourist-points?query=' + query).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  getUserTouristicPoints(userId: number): Observable<TouristicPointModel[]> {
    return this.httpClient.get<TouristicPointModel[]>(this.sharedService.getApiUrl() + 'users/' + userId + '/tourist-points' ).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  addTouristicPointToUser(userId: number, touristPointId: number): Observable<any> {
    return this.httpClient.post<any>(this.sharedService.getApiUrl() + 'users/' + userId + '/tourist-points/' + touristPointId, []).pipe(
      catchError(this.sharedService.handleError)
    );
  } 

  deleteUserTouristicPoint(userId: number, touristPointId: number): Observable<any> {
    return this.httpClient.delete<any>(this.sharedService.getApiUrl() + 'users/' + userId + '/tourist-points/' + touristPointId).pipe(
      catchError(this.sharedService.handleError)
    );
  }

}
