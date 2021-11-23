import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, throwError } from "rxjs";
import { catchError, filter, switchMap, take } from "rxjs/operators";
import { AuthService } from "../services/auth/auth.service";
import { TokenStorageService } from "../services/token/token-storage.service";

const HEADER_TOKEN_KEY = 'Authorization';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    private isRefreshing = false;
    private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
    
    constructor(
        private tokenStorage: TokenStorageService,
        private authService: AuthService
    ) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let authRequest = request;
        const tokenJwt = this.tokenStorage.getToken();
        if (tokenJwt != null) {
            authRequest = this.addTokenToHeader(request, tokenJwt);
        }

        return next.handle(authRequest).pipe(catchError(error => {
            if (error instanceof HttpErrorResponse && !authRequest.url.includes('/signin') && error.status === 401) {
                return this.handle401Error(authRequest, next);
            }

            return throwError(error);
        }));
    }

    private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
        if (!this.isRefreshing) {
            this.isRefreshing = true;
            this.refreshTokenSubject.next(null);

            const token = this.tokenStorage.getRefreshToken();

            if (token) {
                return this.authService.refreshToken(token).pipe(
                    switchMap((token: any) => {
                        this.isRefreshing = false;

                        this.tokenStorage.setToken(token.accessToken);
                        this.refreshTokenSubject.next(token.accessToken);

                        return next.handle(this.addTokenToHeader(request, token.accessToken));
                    }),
                    catchError(error => {
                        this.isRefreshing = false;
                        this.authService.signOut();
                        return throwError(error);
                    })
                );
            }

            return this.refreshTokenSubject.pipe(
                filter(token => token !== null),
                take(1),
                switchMap(token => next.handle(this.addTokenToHeader(request, token)))
            );
        }
    }

    private addTokenToHeader(request: HttpRequest<any>, tokenJwt: string) {
        return request.clone({
            headers: request.headers.set(HEADER_TOKEN_KEY, 'Bearer ' + tokenJwt)
        });
    }

}