import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { User } from 'src/app/models/user.model';
import { SharedService } from '../shared/shared.service';
import { TokenStorageService } from '../token/token-storage.service';

const headerOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private currentUser: BehaviorSubject<User>;

  constructor(
    private httpClient: HttpClient, 
    private sharedService: SharedService,
    private tokenService: TokenStorageService
  ) {
    this.currentUser = new BehaviorSubject<User>(null);
  }

  setUserAuth(userAuth: User) {
    this.currentUser.next(userAuth);
  }

  isUserLoggedIn(): boolean {
    return this.currentUser.value ? true : false;
  }

  getUserAuth(): User {
    return this.currentUser.value;
  }

  signIn(userSignIn: User): Observable<any> {
    return this.httpClient.post<any>(this.sharedService.API_URL + "signin", userSignIn, headerOptions).pipe(
      tap(data => {
        this.tokenService.setToken(data.token);
        this.setUserAuth({ id: data.id, name: data.name, username: data.username, photo: data.photo });
      }),
      catchError(this.sharedService.handleError)
    );
  }

  signUp(userSignUp: User): Observable<any> {
    return this.httpClient.post<any>(this.sharedService.API_URL + 'signup', userSignUp, headerOptions);
  }

  signOut(): Observable<any> {
    return this.httpClient.get<any>(this.sharedService.API_URL + 'signout').pipe(
      catchError(this.sharedService.handleError)
    );
  }

}
