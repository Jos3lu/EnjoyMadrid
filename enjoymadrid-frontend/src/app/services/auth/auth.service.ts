import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { UserModel } from 'src/app/models/user.model';
import { SharedService } from '../shared/shared.service';
import { TokenStorageService } from '../token/token-storage.service';

const headerOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private currentUser: BehaviorSubject<UserModel>;

  constructor(
    private httpClient: HttpClient, 
    private sharedService: SharedService,
    private tokenService: TokenStorageService
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

  signIn(userSignIn: UserModel): Observable<any> {
    return this.httpClient.post<any>(this.sharedService.getApiUrl() + "signin", userSignIn, headerOptions).pipe(
      tap(data => {
        this.tokenService.setToken(data.token);
        this.tokenService.setRefreshToken(data.refreshToken);
        this.setUserAuth({ id: data.id, name: data.name, username: data.username, photo: data.photo });
      }),
      catchError(this.sharedService.handleError)
    );
  }

  signUp(userSignUp: UserModel): Observable<any> {
    return this.httpClient.post<any>(this.sharedService.getApiUrl() + 'signup', userSignUp, headerOptions);
  }

  signOut(): Observable<any> {
    return this.httpClient.get<any>(this.sharedService.getApiUrl() + 'signout').pipe(
      catchError(this.sharedService.handleError)
    );
  }

  refreshToken(token: string) {
    return this.httpClient.post(this.sharedService.getApiUrl() + 'refreshtoken', { refreshToken: token }, headerOptions)
  }

}
