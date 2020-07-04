import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from "@angular/material/sidenav";

@Component({
  selector: 'app-professor',
  templateUrl: './professor.component.html',
  styleUrls: ['./professor.component.css']
})
export class ProfessorComponent implements OnInit {
  corsi = ['Applicazioni Internet', 'Big Data'];
  singoloCorso: string;

  @ViewChild(MatSidenav)
  sidenav: MatSidenav;

  constructor() {
  }

  ngOnInit(): void {
  }

  changeCorso(corso: string) {
    this.singoloCorso = corso;
  }

  toggleMenu() {
    this.sidenav.toggle();
  }
}
