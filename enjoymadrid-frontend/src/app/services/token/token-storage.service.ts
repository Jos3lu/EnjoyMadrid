import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  // Store access & refresh token
  private tokenJwt: BehaviorSubject<string>;
  private refreshTokenJwt: BehaviorSubject<string>;

  constructor() {
    this.tokenJwt = new BehaviorSubject<string>(null);
    this.refreshTokenJwt = new BehaviorSubject<string>(null);
   }

  setToken(token: string) {
    this.tokenJwt.next(token);
  }

  getToken() {
    return this.tokenJwt.value;
  }

  setRefreshToken(refreshToken: string) {
    this.refreshTokenJwt.next(refreshToken);
  }

  getRefreshToken() {
    return this.refreshTokenJwt.value;
  }

}
