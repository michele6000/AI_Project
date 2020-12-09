import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {UserModel} from '../models/user.models';
import {AuthService} from '../auth/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {
  error = false;
  notValidDomain = false;
  file: File;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {
  }

  ngOnInit(): void {
  }

  register(f: NgForm) {
    if (this.file === undefined) {
      this.errorMessage = 'You must select a profile image';
      this.error = true;
      return;
    }
    if (f.value.password !== f.value.confirm_password) {
      this.errorMessage = 'Different password';
      this.error = true;
      return;
    }

    if (!f.value.matricola.startsWith('d') && !f.value.matricola.startsWith('s')) {
      this.errorMessage = 'Matricola should start with d or s.';
      this.error = true;
      return;
    }

    if ((f.value.matricola.startsWith('d') && f.value.email !== (f.value.matricola + '@polito.it'))
    || f.value.matricola.startsWith('s') && f.value.email !== (f.value.matricola + '@studenti.polito.it')) {
      this.errorMessage = 'Email must start with dXXXXXX or sXXXXXX and it must belong to a university domain.';
      this.error = true;
      return;
    }

    this.error = false;
    let user = new UserModel();
    user.firstName = f.value.name;
    user.name = f.value.surname;
    user.username = f.value.matricola;
    user.password = f.value.password;
    user.email = f.value.email;
    this.authService.register(user, this.file).subscribe(result => {
      if (result === false) {
        this.notValidDomain = true;
      } else {
        this.router.navigate(['home'], {queryParams: {doLogin: 'true'}});
      }
    });

  }

  handleFileSelect($event: any) {
    this.file = $event.target.files[0];
  }
}
