import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User } from 'src/app/models/user.model';
import { SharedService } from '../shared/shared.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient, private sharedService: SharedService) { }

  getUser(id: number): Observable<User> {
    return this.httpClient.get<User>(this.sharedService.API_URL + 'users/' + id).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  getUserByUsername(username: string): Observable<User> {
    return this.httpClient.get<User>(this.sharedService.API_URL + 'users?username=' + username).pipe(
      catchError(this.sharedService.handleError)
    );
  }

}
