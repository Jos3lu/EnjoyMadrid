import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VarService {

  API_URL = "http://localhost:8080/api/";

  constructor() { }
}
