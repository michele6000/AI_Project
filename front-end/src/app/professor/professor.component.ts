import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {CourseModel} from "../models/course.model";
import {switchMap} from "rxjs/operators";

@Component({
  selector: 'app-professor',
  templateUrl: './professor.component.html',
  styleUrls: ['./professor.component.css']
})
export class ProfessorComponent implements OnInit {
  corsi: CourseModel[] = [
    {name: 'Applicazioni Internet', identifier: 'AI', min: 2, max: 4},
    {name: 'Big Data', identifier: 'BD', min: 3, max: 4}
  ];
  singoloCorso: CourseModel;

  @ViewChild(MatSidenav)
  sidenav: MatSidenav;

  constructor(private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(param => {
      const courseName = param.get('course');
      const course = this.corsi.filter(c => c.name.toLowerCase().replace(' ', '-') === courseName);
      if (course.length > 0) {
        this.changeCorso(course[0]);
        this.singoloCorso = course[0];
      } else {
        this.router.navigate(['teacher', this.corsi[0].name.toLowerCase().replace(' ', '-')]);
      }
    });
  }

  changeCorso(corso: CourseModel) {
    this.singoloCorso = corso;
    this.router.navigate(['teacher', corso.name.toLowerCase().replace(' ', '-'), 'students']).then();
  }

  toggleMenu() {
    this.sidenav.toggle();
  }

  manageCourses() {
    localStorage.setItem('url_teacher', this.router.routerState.snapshot.url);
  }
}
