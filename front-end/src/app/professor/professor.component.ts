import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {Router} from "@angular/router";

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

  constructor(private router: Router) {
  }

  ngOnInit(): void {
  }

  changeCorso(corso: string) {
    this.singoloCorso = corso;
  }

  toggleMenu() {
    this.sidenav.toggle();
  }

  manageCourses() {
    localStorage.setItem('url_teacher', this.router.routerState.snapshot.url);
  }
}
