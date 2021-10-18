import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  private tokenJwt: BehaviorSubject<string>;

  constructor() {
    this.tokenJwt = new BehaviorSubject<string>(null);
   }

  setToken(token: string) {
    this.tokenJwt.next(token);
  }

  getToken() {
    return this.tokenJwt.value;
  }

}
