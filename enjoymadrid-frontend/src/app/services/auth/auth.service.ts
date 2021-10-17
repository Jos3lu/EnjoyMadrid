import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User } from 'src/app/models/user.model';
import { SharedService } from '../shared/shared.service';

const headerOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private httpClient: HttpClient, private sharedService: SharedService) { }

  signIn(userSignIn: User): Observable<any> {
    return this.httpClient.post<User>(this.sharedService.API_URL + "signin", userSignIn, headerOptions).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  signUp(userSignUp: User): Observable<User> {
    return this.httpClient.post<User>(this.sharedService.API_URL + 'signup', userSignUp, headerOptions).pipe(
      catchError(this.sharedService.handleError)
    );
  }

}
