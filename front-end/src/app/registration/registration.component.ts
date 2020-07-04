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

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
  }

  register(f: NgForm) {
    let user = new UserModel();
    user.nome = f.value.nome;
    user.cognome = f.value.cognome;
    user.matricola = f.value.matricola;
    user.password = f.value.password;
    user.email = f.value.email;

    this.authService.register(user);
  }
}
