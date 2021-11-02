import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RouteModel } from 'src/app/models/route.model';
import { SharedService } from '../shared/shared.service';

const headerOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class RouteService {

  constructor(
    private httpClient: HttpClient,
    private sharedService: SharedService
  ) { }

  createRoute(route: RouteModel): Observable<any> {
    return this.httpClient.post(this.sharedService.API_URL + 'routes', route, headerOptions).pipe(
      catchError(this.sharedService.handleError)
    );
  }
  
}
