import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { TokenStorageService } from "../services/token/token-storage.service";

const HEADER_TOKEN_KEY = 'Authorization';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    
    constructor(private tokenStorage: TokenStorageService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let authRequest = req;
        const tokenJwt = this.tokenStorage.getToken();
        if (tokenJwt != null) {
            authRequest = req.clone({
                headers: req.headers.set(HEADER_TOKEN_KEY, 'Bearer ' + tokenJwt)
            });
        }
        return next.handle(authRequest);
    }

}