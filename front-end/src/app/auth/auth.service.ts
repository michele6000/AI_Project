import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {UserModel} from '../models/user.models';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {shareReplay, tap} from 'rxjs/operators';
import * as moment from 'moment';
import {UserLogged} from '../models/user-logged';
import {Router} from '@angular/router';
import {ProfessorService} from '../services/professor.service';
import {StudentService} from '../services/student.service';

const API_URL_LOGIN = '/api/auth/signin';
const API_URL_REGISTER = '/api/auth/';
const DOMINIO_PROFESSOR = '@polito.it';
const DOMINIO_STUDENT = '@studenti.polito.it';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  user: Observable<UserLogged>;
  localUser: UserLogged = {
    id: null, email: undefined, roles: [], image: null
  };
  tokenExpired: Observable<boolean> = new Observable<boolean>();
  private userSubject: BehaviorSubject<UserLogged>;
  private tokenExpiredSubject: BehaviorSubject<boolean>;

  constructor(private http: HttpClient, private router: Router,
              private professorService: ProfessorService, private studentService: StudentService) {
    this.userSubject = new BehaviorSubject<UserLogged>(this.localUser);
    this.user = this.userSubject.asObservable();
    this.tokenExpiredSubject = new BehaviorSubject<boolean>(false);
    this.tokenExpired = this.tokenExpiredSubject.asObservable();
    if (localStorage.getItem('token')) {
      // this.isUserLoggedIn = true;
      const tkn = JSON.parse(atob(localStorage.getItem('token').split('.')[1]));
      this.localUser.email = localStorage.getItem('email');
      this.localUser.roles = tkn.roles;
      this.localUser.id = localStorage.getItem('email').split('@')[0];
      this.localUser.image = localStorage.getItem('image');
      this.loginRetrieveDatas();
      // TODO: Verificare, serve per riportare alla pagina corretta in base al ruolo dell'utente
      // dopo che vengono richiesti i dati
      this.loginRedirect();
    } else {
      this.userSubject.next(null);
    }
  }

  loginRedirect() {
    if (this.localUser.roles.filter((value => value === 'ROLE_STUDENT')).length > 0) {
      this.router.navigate(['student']);
    } else if (this.localUser.roles.filter((value => value === 'ROLE_PROFESSOR')).length > 0) {
      this.router.navigate(['teacher']);
    } else if (this.localUser.roles.filter((value => value === 'ROLE_ADMIN')).length > 0) {
      // this.router.navigate(['home']);
    } else {
      // Utente loggato senza ruoli
    }
  }

  loginRetrieveDatas() {
    if (this.localUser.roles.filter((value => value === 'ROLE_STUDENT')).length > 0) {
      this.studentService.findCoursesByStudent(this.localUser.id);
      this.studentService.findTeamsByStudent(this.localUser.id);
    } else if (this.localUser.roles.filter((value => value === 'ROLE_PROFESSOR')).length > 0) {
      this.professorService.findCoursesByProfessor(this.localUser.id);
    } else {
      // Utente ADMIN o senza ha ruoli
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
          localStorage.setItem('image', payload.image);

          this.localUser.roles = tkn.roles;
          this.localUser.id = email.split('@')[0];
          this.localUser.email = email;

          // this.isUserLoggedIn = true;
          this.userSubject.next(this.localUser);
        },
        (error: any) => {
          this.userSubject.next(null);
          // propago l'evento se non è andato a buon fine la login -> in login.ts lo ascolto nel costruttore
          // this.userLogged.emit(false);
        }
      ), shareReplay());
  }

  clearStorage() {
    localStorage.removeItem('token');
    localStorage.removeItem('expires_at');
    localStorage.removeItem('email');
    localStorage.removeItem('id');
    localStorage.removeItem('roles');
  }

  logout() {
    this.clearStorage();
    this.userSubject.next(null);
    this.tokenExpiredSubject.next(null);
    this.router.navigate(['home']);
  }

  register(user: UserModel,  file: File) {
    const email = user.email;
    let url = '';
    if (email.includes(DOMINIO_PROFESSOR)) {
      url = 'addProfessor';
    } else if (email.includes(DOMINIO_STUDENT)) {
      url = 'addStudent';
    }

    const formData = new FormData();
    const submissionStr = new Blob([JSON.stringify(user)], { type: 'application/json'});
    formData.append('user', submissionStr);
    formData.append('file', file);

    if (url !== '') {
      return this.http.post(
        API_URL_REGISTER + url,
        formData,
      );
    } else {
      return of(false);
    }
  }

  public isLoggedIn() {
    if (localStorage.getItem('expires_at') == null) {
      return false;
    }
    if (!moment().isBefore(moment.unix(+localStorage.getItem('expires_at')))) {
      // Il token non è più valido
      this.clearStorage();
      this.userSubject.next(null);

      // @todo Valutare altra soluzione
      this.tokenExpiredSubject.next(true);
      this.tokenExpiredSubject.next(false);

      return false;
    } else {
      return true;
    }
  }
}
