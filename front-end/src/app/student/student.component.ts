import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from "@angular/material/sidenav";
import {CourseModel} from "../models/course.model";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-student',
  templateUrl: './student.component.html',
  styleUrls: ['./student.component.css']
})
export class StudentComponent implements OnInit {

  corsi: CourseModel[] = [
    {name: 'Applicazioni Internet', identifier: 'AI', min: 2, max: 4},
    {name: 'Big Data', identifier: 'BD', min: 3, max: 4}
  ];

  singoloCorso: CourseModel;

  @ViewChild(MatSidenav)
  sidenav: MatSidenav;

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
  }

  toggleMenu() {
    this.sidenav.toggle();
  }

  changeCorso(corso: CourseModel) {
    this.singoloCorso = corso;
    this.router.navigate(['student', corso.name.toLowerCase().replace(' ', '-')]).then();
  }
}
