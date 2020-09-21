import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {AuthService} from '../../auth/auth.service';
import {Router} from '@angular/router';
import {DomSanitizer} from "@angular/platform-browser";

@Component({
  selector: 'app-logged-header',
  templateUrl: './logged-header.component.html',
  styleUrls: ['./logged-header.component.css']
})
export class LoggedHeaderComponent implements OnInit {
  email = '';
  username = '';
  isLoggedIn = false;
  image: any;
  ImgUrl: any;

  @Output() toggleMenu: EventEmitter<any> = new EventEmitter<any>();
  title = 'HOME';

  constructor(private authService: AuthService, private router: Router, private _DomSanitizationService: DomSanitizer) {
    this.authService.user.subscribe(next => {
      if (next != null) {
        this.email = next.email;
        this.username = this.email.split("@")[0];
        this.isLoggedIn = true;
        this.title = 'VIRTUAL LABS';
        this.ImgUrl = next.image

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
function _arrayBufferToBase64( buffer ) {
  let binary = '';
  const bytes = new Uint8Array(buffer);
  const len = bytes.byteLength;
  for (let i = 0; i < len; i++) {
    binary += String.fromCharCode( bytes[ i ] );
  }
  return window.btoa( binary );
}
