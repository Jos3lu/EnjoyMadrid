import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { User } from 'src/app/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  loginUrl = "http://localhost:8080/api/login";
  logoutUrl = "http://localhost:8080/api/logout/";

  isUserLoggedIn: boolean = false;

  constructor(private httpClient: HttpClient) { }

  login() {
    
  }

}
