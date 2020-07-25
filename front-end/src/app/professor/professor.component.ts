import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {CourseModel} from '../models/course.model';
import {switchMap} from 'rxjs/operators';
import {CrudService} from "../services/crud.service";
import {AuthService} from "../auth/auth.service";

@Component({
  selector: 'app-professor',
  templateUrl: './professor.component.html',
  styleUrls: ['./professor.component.css']
})
export class ProfessorComponent implements OnInit {
  corsi: CourseModel[] = [];
  singoloCorso: CourseModel;

  @ViewChild(MatSidenav)
  sidenav: MatSidenav;

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService, private crudService: CrudService) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(param => {
      const courseName = param.get('course');
      const course = this.corsi.filter(c => c.name.toLowerCase().replace(' ', '-') === courseName);
      if (course.length > 0) {
        this.changeCorso(course[0]);
      } else if (this.corsi.length > 0) {
        this.router.navigate(['teacher', this.corsi[0].name.toLowerCase().replace(' ', '-')]);
      }
    });

    this.authService.user.subscribe((user) => {
      if (user != null) {
        this.crudService.findCoursesByProfessor('1').subscribe(
          (courses) => this.corsi = courses
        );
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
