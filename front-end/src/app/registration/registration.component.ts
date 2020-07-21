import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {UserModel} from "../models/user.models";
import {AuthService} from "../auth/auth.service";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  error_pw: boolean = false;

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
  }

  register(f: NgForm) {
    if (f.value.password !== f.value.confirm_password) {
      this.error_pw = true;
    } else {
      let user = new UserModel();
      user.name = f.value.name;
      user.surname = f.value.surname;
      user.matricola = f.value.matricola;
      user.password = f.value.password;
      user.email = f.value.email;

      this.authService.register(user);
    }
  }
}
