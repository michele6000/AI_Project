import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserModel} from '../models/user.models';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {shareReplay, tap} from 'rxjs/operators';
import * as moment from 'moment';
import {UserLogged} from '../models/user-logged';
import {Router} from '@angular/router';
import {ProfessorService} from '../services/professor.service';
import {StudentService} from "../services/student.service";

const API_URL_LOGIN = '/api/auth/signin';
const API_URL_REGISTER = '/api/auth/';
const DOMINIO_PROFESSOR = '@polito.it';
const DOMINIO_STUDENT = '@studenti.polito.it';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  user: Observable<UserLogged>;
  private userSubject: BehaviorSubject<UserLogged>;
  localUser: UserLogged = {
    id: null, email: undefined, roles: []
  };

  constructor(private http: HttpClient, private router: Router, private professorService: ProfessorService, private studentService: StudentService) {
    this.userSubject = new BehaviorSubject<UserLogged>(this.localUser);
    this.user = this.userSubject.asObservable();
    if (localStorage.getItem('token')) {
      // this.isUserLoggedIn = true;
      const tkn = JSON.parse(atob(localStorage.getItem('token').split('.')[1]));
      this.localUser.email = localStorage.getItem('email');
      this.localUser.roles = tkn.roles;
      this.localUser.id = localStorage.getItem('email').split('@')[0];
      // Verificare
      this.loginRedirect();
    } else {
      this.userSubject.next(null);
    }
  }

  loginRedirect() {
    if (this.localUser.roles.filter((value => value === 'ROLE_STUDENT')).length > 0) {
      this.studentService.findCoursesByStudent(this.localUser.id);
      this.router.navigate(['student']);
    } else if (this.localUser.roles.filter((value => value === 'ROLE_PROFESSOR')).length > 0) {
      this.professorService.findCoursesByProfessor(this.localUser.id);
      this.router.navigate(['teacher']);
    } else if (this.localUser.roles.filter((value => value === 'ROLE_ADMIN')).length > 0) {
      this.router.navigate(['home']);
    } else {
      // @todo Utente loggato ma non ha ruoli
    }
  }

  login(email: string, password: string) {
    return this.http.post(
      API_URL_LOGIN, {
        username: email,
        password
      }
    ).pipe(
      tap((payload: any) => {
          const tkn = JSON.parse(atob(payload.token.split('.')[1]));
          localStorage.setItem('token', payload.token);
          localStorage.setItem('expires_at', tkn.exp);
          localStorage.setItem('email', email);
          localStorage.setItem('id', email.split('@')[0]);
          localStorage.setItem('roles', tkn.roles);

          this.localUser.roles = tkn.roles;
          this.localUser.id = email.split('@')[0];

          // this.isUserLoggedIn = true;
          this.userSubject.next(this.localUser);
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
          this.userSubject.next(null);
          // propago l'evento se non è andato a buon fine la login -> in login.ts lo ascolto nel costruttore
          // this.userLogged.emit(false);
        }
      ), shareReplay());
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('expires_at');
    localStorage.removeItem('email');
    localStorage.removeItem('id');
    localStorage.removeItem('roles');

    this.userSubject.next(null);
    this.router.navigate(['home']);
  }

  register(user: UserModel) {
    const email = user.email;
    let url = '';
    if (email.includes(DOMINIO_PROFESSOR)) {
      url = 'addProfessor';
    } else if (email.includes(DOMINIO_STUDENT)) {
      url = 'addStudent';
    }

    if (url !== '') {
      return this.http.post(
        API_URL_REGISTER + url,
        user
      );
    } else {
      return of(false);
    }
  }

  public isLoggedIn() {
    if (localStorage.getItem('expires_at') == null) {
      return false;
    }
    return moment().isBefore(moment.unix(+localStorage.getItem('expires_at')));
  }
}
