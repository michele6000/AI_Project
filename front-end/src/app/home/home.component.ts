import {Component, OnInit} from '@angular/core';
import {AuthService} from "../auth/auth.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  data = [
    {
      name: 'Mario',
      email: 'mario@rossi.it'
    },
    {
      name: 'Giovanni',
      email: 'giovanni@verdi.it'
    }
  ];
  columns = ['name', 'email'];

  constructor(private authService: AuthService) {
    console.log(this.columns);
  }

  ngOnInit(): void {
  }

}
