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
export class UserService {

  constructor(private httpClient: HttpClient, private sharedService: SharedService) { }

  getUser(id: number): Observable<User> {
    return this.httpClient.get<User>(this.sharedService.API_URL + 'users/' + id).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  updateUser(id: number, user: User): Observable<User> {
    return this.httpClient.put<User>(this.sharedService.API_URL + 'users/' + id, user, headerOptions);
  }

  updateUserPictureProfile(id: number, imageForm: FormData): Observable<User> {
    return this.httpClient.put<User>(this.sharedService.API_URL + 'users/' + id + '/picture', imageForm);
  }

  deleteUser(id: number): Observable<any> {
    return this.httpClient.delete<any>(this.sharedService.API_URL + 'users/' + id).pipe(
      catchError(this.sharedService.handleError)
    )
  }

}
