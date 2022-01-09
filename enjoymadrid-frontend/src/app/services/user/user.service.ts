import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UserModel } from 'src/app/models/user.model';
import { SharedService } from '../shared/shared.service';

const headerOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private httpClient: HttpClient, 
    private sharedService: SharedService
  ) { }

  getUser(id: number): Observable<any> {
    return this.httpClient.get(this.sharedService.getApiUrl() + 'users/' + id).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  updateUser(id: number, user: UserModel): Observable<any> {
    return this.httpClient.put(this.sharedService.getApiUrl() + 'users/' + id, user, headerOptions);
  }

  updateUserPictureProfile(id: number, imageForm: FormData): Observable<any> {
    return this.httpClient.put(this.sharedService.getApiUrl() + 'users/' + id + '/picture', imageForm);
  }

  deleteUser(id: number): Observable<any> {
    return this.httpClient.delete(this.sharedService.getApiUrl() + 'users/' + id).pipe(
      catchError(this.sharedService.handleError)
    )
  }

}
