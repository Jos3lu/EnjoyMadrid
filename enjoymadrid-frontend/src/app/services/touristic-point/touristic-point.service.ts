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

}
