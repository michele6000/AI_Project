import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {ActivatedRoute, Router} from '@angular/router';
import {CourseModel} from '../models/course.model';
import {ProfessorService} from "../services/professor.service";
import {AuthService} from "../auth/auth.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-professor',
  templateUrl: './professor.component.html',
  styleUrls: ['./professor.component.css']
})
export class ProfessorComponent implements OnInit, OnDestroy {
  corsi: CourseModel[] = [];
  singoloCorso: CourseModel;

  @ViewChild(MatSidenav)
  sidenav: MatSidenav;

  s1: Subscription;

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService, private professorService: ProfessorService) {
    this.s1 = this.professorService.courses.subscribe((next) => {
      if (next) {
        this.corsi = next;
        this.checkUrl();
      } else {
        this.corsi = [];
      }
    });
  }

  ngOnDestroy() {
    this.s1.unsubscribe();
  }

  ngOnInit(): void {
  }

  checkUrl() {
    this.route.paramMap.subscribe(param => {
      const courseName = param.get('course');
      const course = this.corsi.filter(c => c.name.toLowerCase().replace(/\s+/g, '-') === courseName);
      if (course.length > 0) {
        this.changeCorso(course[0]);
      } else if (this.corsi.length > 0) {
        this.router.navigate(['teacher', this.corsi[0].name.toLowerCase().replace(/\s+/g, '-')]);
      } else {
        console.log('Nessun corso!');
      }
    });
  }

  changeCorso(corso: CourseModel) {
    this.singoloCorso = corso;
    this.router.navigate(['teacher', corso.name.toLowerCase().replace(/\s+/g, '-'), 'students']).then();
    this.professorService.eventsSubjectChangeCorsoSideNav.next();
  }

  toggleMenu() {
    this.sidenav.toggle();
  }

  manageCourses() {
    localStorage.setItem('url_teacher', this.router.routerState.snapshot.url);
  }
}
