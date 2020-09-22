import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {UserModel} from "../models/user.models";
import {AuthService} from "../auth/auth.service";
import {Router} from '@angular/router';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  errorPw = false;
  notValidDomain = false;
  file: File;

  constructor(private authService: AuthService, private router: Router) {
  }

  ngOnInit(): void {
  }

  register(f: NgForm) {
    if (f.value.password !== f.value.confirm_password) {
      this.errorPw = true;
    } else {
      let user = new UserModel();
      user.firstName = f.value.name;
      user.name = f.value.surname;
      user.id = f.value.matricola;
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
  }

  handleFileSelect($event: any) {
    this.file = $event.target.files[0];
  }
}
