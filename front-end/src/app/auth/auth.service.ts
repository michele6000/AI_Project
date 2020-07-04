import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserModel} from "../models/user.models";

const API_URL_LOGIN = 'http://localhost:3000/login';
const API_URL_REGISTER = 'http://localhost:3000/register';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {
  }

  login(email: string, password: string) {
    this.http.post(
      API_URL_LOGIN, {
        email: email,
        password: password
      }
    ).subscribe(
      (payload: any) => {
        /*
        this.setSession(payload, email);
        let user : UserModule = new UserModule();
        user.email = email;
        user.accessToken = payload.accessToken;
        user.isLogged = true;
        this.userSubject.next(user);
        this.userLogged.emit(true);
        */
      },
      (error: any) => {
        // this.userLogged.emit(false);    // propago l'evento se non è andato a buon fine la login -> in login.ts lo ascolto nel costruttore
      }
    );
  }

  register(user: UserModel) {
    this.http.post(
      API_URL_REGISTER,
      {
        user: user
      }
    ).subscribe(
      (payload: any) => {

      },
      (error: any) => {

      }
    )
  }
}
