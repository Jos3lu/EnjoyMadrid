import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { UserModel } from 'src/app/models/user.model';
import { SharedService } from '../shared/shared.service';
import { StorageService } from '../storage/storage.service';
import { TokenStorageService } from '../token/token-storage.service';

const headerOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // Store user logged in
  private currentUser: BehaviorSubject<UserModel>;

  constructor(
    private httpClient: HttpClient, 
    private sharedService: SharedService,
    private tokenService: TokenStorageService,
    private storageService: StorageService
  ) {
    this.currentUser = new BehaviorSubject<UserModel>(null);
  }

  setUserAuth(userAuth: UserModel) {
    this.currentUser.next(userAuth);
  }

  isUserLoggedIn(): boolean {
    return this.currentUser.value ? true : false;
  }

  getUserAuth(): UserModel {
    return this.currentUser.value;
  }

  getUserAuthChange(): BehaviorSubject<UserModel> {
    return this.currentUser;
  }

  signIn(userSignIn: UserModel): Observable<any> {
    return this.httpClient.post<any>(this.sharedService.getApiUrl() + "signin", userSignIn, headerOptions).pipe(
      tap(data => {
        // Set access & refresh token of user, & set information of user
        this.tokenService.setToken(data.token);
        this.tokenService.setRefreshToken(data.refreshToken);
        this.setUserAuth({ id: data.id, name: data.name, username: data.username, photo: data.photo });
        this.sharedService.setRoutes(data.routes);
        this.sharedService.setTouristicPoints(data.touristicPoints)
      }),
      catchError(this.sharedService.handleError)
    );
  }

  signUp(userSignUp: UserModel): Observable<any> {
    return this.httpClient.post<any>(this.sharedService.getApiUrl() + 'signup', userSignUp, headerOptions).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  signOut(): Observable<any> {
    return this.httpClient.post<any>(this.sharedService.getApiUrl() + 'signout', null).pipe(
      tap(_ => {
        // Clear data of user
        this.setUserAuth(null);
        this.tokenService.setToken(null);
        this.tokenService.setRefreshToken(null);
        // Set routes of local storage
        this.storageService.get('routes').then(routes => {
          if (!routes) {
            routes = [];
            this.storageService.set('routes',routes);
          }
          this.sharedService.setRoutes(routes);
        }).catch(error => {
          console.log(error);
        });
        // Set interest points of local storage
        this.storageService.get('touristicPoints').then(touristicPoints => {
          if (!touristicPoints) {
            touristicPoints = [];
            this.storageService.set('touristicPoints', touristicPoints);
          }
          this.sharedService.setTouristicPoints(touristicPoints);
        });
      }),
      catchError(this.sharedService.handleError)
    );
  }

  refreshToken(token: string): Observable<any> {
    return this.httpClient.post<any>(this.sharedService.getApiUrl() + 'refreshtoken', { refreshToken: token }, headerOptions)
  }

}
