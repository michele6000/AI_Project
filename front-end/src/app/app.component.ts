import {Component, OnInit} from '@angular/core';
import {AuthService} from './auth/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {LoginComponent} from './login/login.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'front-end';

  constructor(private authService: AuthService, private router: Router, private activeRoute: ActivatedRoute, private dialog: MatDialog) {
  }

  ngOnInit() {
    this.activeRoute.queryParamMap
      .subscribe(params => {
        if (params.has('doLogin') && params.get('doLogin') === 'true') {
          this.dialog.open(LoginComponent, {disableClose: true})
            .afterClosed()              // dopo la chiusura del dialog faccio le redirect
            .subscribe(result => {
              if (result) {              // se result è false => click su CANCEL -> redirect a HOME
                /* let myurl = this.authService.getLastUrl();
                if(myurl != null){
                  this.authService.deleteUrl();
                  this.router.navigateByUrl(myurl);         // redirect all'ultimo url
                } else {
                  this.router.navigate(['home']);
                }*/
                this.authService.loginRetrieveDatas();
                this.authService.loginRedirect();
              } else {
                this.router.navigate(['home']);
              }
            });
        }
      });
  }

}
