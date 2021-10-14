import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User } from 'src/app/models/user.model';
import { VarService } from '../var/var.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient, private varService: VarService) { }

  getUser(id: number): Observable<User> {
    return this.httpClient.get(this.varService.API_URL + id).pipe(
      catchError(this.handleError)
    ) as Observable<User>;
  }


  private handleError(handleError: HttpErrorResponse) {
    if (handleError.status === 0) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', handleError.error);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong.
      console.error(`Backend returned code ${handleError.status}, body was: `, handleError.error);
    }
    // Return an observable with a user-facing error message.
    return throwError(
      'Something bad happened; please try again later.');
  }

}
