import {Component, OnInit} from '@angular/core';
import {AuthService} from '../auth/auth.service';
import {NgForm} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  error = false;

  constructor(private dialogRef: MatDialogRef<LoginComponent>, private authService: AuthService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.authService.tokenExpired.subscribe((expired) => {
      if (expired) {
        this.snackBar.open('Token expired, please sign in again.', 'OK', {
          duration: 5000
        });
      }
    });
  }

  login(form: NgForm) {
    this.authService.login(form.value.email, form.value.password).subscribe(
      (res) => this.dialogRef.close(res),
      (error) => this.error = true
    );
  }
}
