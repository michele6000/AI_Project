import { Component, OnInit } from '@angular/core';
import {AuthService} from "../auth/auth.service";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  error: boolean = false;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
  }

  login(form: NgForm){
    this.authService.login(form.value.email, form.value.password);
  }
}
