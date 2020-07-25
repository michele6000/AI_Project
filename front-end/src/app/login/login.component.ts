import { Component, OnInit } from '@angular/core';
import {AuthService} from "../auth/auth.service";
import {NgForm} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  error = false;

  constructor(private dialogRef: MatDialogRef<LoginComponent>, private authService: AuthService) { }

  ngOnInit(): void {
  }

  login(form: NgForm){
    this.authService.login(form.value.email, form.value.password).subscribe(
      (res) => this.dialogRef.close(res),
      (error) => this.error = true
    );
  }
}
