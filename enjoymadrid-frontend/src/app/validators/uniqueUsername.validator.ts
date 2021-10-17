import { Directive } from "@angular/core";
import { AbstractControl, AsyncValidator, NG_ASYNC_VALIDATORS, ValidationErrors } from "@angular/forms";
import { Observable, of } from "rxjs";
import { catchError, map } from "rxjs/operators";
import { UserService } from "../services/user/user.service";

@Directive({
    selector: '[uniqueusernamevalidator]',
    providers: [{provide: NG_ASYNC_VALIDATORS, useExisting: UniqueUsernameValidator, multi: true}]
  })
export class UniqueUsernameValidator implements AsyncValidator {
  
    constructor(private userService: UserService) {}


    validate(control: AbstractControl): Promise<ValidationErrors> | Observable<ValidationErrors> {
        return this.userService.getUserByUsername(control.value).pipe(
            map(user => (user ? { usernameTaken: true } : null)),
            catchError(() => of(null))
        );
    }

}