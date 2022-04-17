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

  updateUser(id: number, user: UserModel): Observable<any> {
    return this.httpClient.put(this.sharedService.getApiUrl() + 'users/' + id, user, headerOptions).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  updateUserPictureProfile(id: number, imageForm: FormData): Observable<any> {
    return this.httpClient.put(this.sharedService.getApiUrl() + 'users/' + id + '/picture', imageForm).pipe(
      catchError(this.sharedService.handleError)
    );
  }

  deleteUser(id: number): Observable<any> {
    return this.httpClient.delete(this.sharedService.getApiUrl() + 'users/' + id).pipe(
      catchError(this.sharedService.handleError)
    )
  }

}
