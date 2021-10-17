import { Injectable } from '@angular/core';
import { Storage } from '@capacitor/storage';

const KEY_TOKEN = 'token-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() { }

  async signOut() {
    await Storage.clear();
  }

  async saveToken(token: string) {
    await Storage.remove({ key : KEY_TOKEN })
    await Storage.set({ 
      key: KEY_TOKEN,
      value: token,
     })
  }

  getToken() {
    const token = Storage.get({ key: KEY_TOKEN });
    return token;
  }

}
