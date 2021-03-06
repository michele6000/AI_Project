import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {AuthService} from '../../auth/auth.service';
import {Router} from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';
import {Observable, Observer} from 'rxjs';

const API_URL_PUBLIC = '/public/auth/';
const API_URL_LOCAL = '/local/auth/';


@Component({
  selector: 'app-logged-header',
  templateUrl: './logged-header.component.html',
  styleUrls: ['./logged-header.component.css']
})
export class LoggedHeaderComponent implements OnInit {
  email = '';
  name = '';
  firstName = '';
  username = '';
  isLoggedIn = false;
  image: any;
  ImgUrl: any;
  base64Image: any;


  @Output() toggleMenu: EventEmitter<any> = new EventEmitter<any>();
  title = 'HOME';

  constructor(private authService: AuthService, private router: Router, private _DomSanitizationService: DomSanitizer) {
    this.authService.user.subscribe(next => {
      if (next != null) {
        this.email = next.email;
        this.username = this.email.split('@')[0];
        this.isLoggedIn = true;
        this.title = 'Teams';
        this.ImgUrl = next.image;
        this.name = next.name;
        this.firstName = next.firstName;

      } else {
        this.email = '';
        this.isLoggedIn = false;
        this.title = 'HOME';
      }
    });
  }

  ngOnInit() {
    const imageUrl = API_URL_PUBLIC + 'getImage?id=' + this.username;
    this.getBase64ImageFromURL(imageUrl).subscribe(base64data => {
      this.base64Image = 'data:image/jpg;base64,' + base64data;
    });
  }

  toggleForMenuClick() {
    this.toggleMenu.emit();
  }

  logout() {
    this.authService.logout();
  }

  getBase64ImageFromURL(url: string) {
    return Observable.create((observer: Observer<string>) => {
      let img = new Image();
      img.crossOrigin = 'Anonymous';
      img.src = url;
      img.src = url;
      if (!img.complete) {
        img.onload = () => {
          observer.next(this.getBase64Image(img));
          observer.complete();
        };
        img.onerror = (err) => {
          observer.error(err);
        };
      } else {
        observer.next(this.getBase64Image(img));
        observer.complete();
      }
    });
  }

  getBase64Image(img: HTMLImageElement) {
    var canvas = document.createElement('canvas');
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext('2d');
    ctx.drawImage(img, 0, 0);
    var dataURL = canvas.toDataURL('image/png');
    return dataURL.replace(/^data:image\/(png|jpg);base64,/, '');
  }

}
