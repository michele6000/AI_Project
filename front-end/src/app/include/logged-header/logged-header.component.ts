import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {AuthService} from '../../auth/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-logged-header',
  templateUrl: './logged-header.component.html',
  styleUrls: ['./logged-header.component.css']
})
export class LoggedHeaderComponent implements OnInit {
  email = '';
  isLoggedIn = false;

  @Output() toggleMenu: EventEmitter<any> = new EventEmitter<any>();
  title = 'HOME';

  constructor(private authService: AuthService, private router: Router) {
    this.authService.user.subscribe(next => {
      if (next != null) {
        this.email = next.email;
        this.isLoggedIn = true;
        this.title = 'VIRTUAL LABS';
      } else {
        this.email = '';
        this.isLoggedIn = false;
        this.title = 'HOME';
      }
    });
  }

  ngOnInit(): void {
  }

  toggleForMenuClick() {
    this.toggleMenu.emit();
  }

  logout() {
    this.authService.logout();
  }
}
