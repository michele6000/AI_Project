import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {UserModel} from "../models/user.models";
import {AuthService} from "../auth/auth.service";
import {Router} from '@angular/router';
import {register} from 'ts-node';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  errorPw = false;
  notValidDomain = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
  }

  register(f: NgForm) {
    if (f.value.password !== f.value.confirm_password) {
      this.errorPw = true;
    } else {
      let user = new UserModel();
      user.name = f.value.name;
      user.surname = f.value.surname;
      user.matricola = f.value.matricola;
      user.password = f.value.password;
      user.email = f.value.email;

      this.authService.register(user).subscribe(result => {
        if (result === false){
          this.notValidDomain = true;
        } else {
          this.router.navigate(['home?doLogin=true']);
        }
      });
    }
  }
}
