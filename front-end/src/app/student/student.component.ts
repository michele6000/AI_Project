import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {CourseModel} from '../models/course.model';
import {ActivatedRoute, Router} from '@angular/router';
import {StudentService} from '../services/student.service';
import {Subscription} from 'rxjs';
import {ProfessorModel} from '../models/professor.model';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-student',
  templateUrl: './student.component.html',
  styleUrls: ['./student.component.css']
})
export class StudentComponent implements OnInit {

  corsi: CourseModel[] = [];
  professors: ProfessorModel[] = [];

  singoloCorso: CourseModel;

  @ViewChild(MatSidenav)
  sidenav: MatSidenav;

  s1: Subscription;
  professorsList = '';

  constructor(private route: ActivatedRoute, private router: Router, private studentService: StudentService, private snackBar: MatSnackBar) {
    this.s1 = this.studentService.courses.subscribe((next) => {
        if (next) {
          this.corsi = next;
          this.checkUrl();
        } else {
          this.corsi = [];
        }
      },
      error => {
        this.genericError();
      });
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
        this.router.navigate(['student', this.corsi[0].name.toLowerCase().replace(/\s+/g, '-')]);
      }
    });
  }

  toggleMenu() {
    this.sidenav.toggle();
  }

  changeCorso(corso: CourseModel) {
    this.singoloCorso = corso;
    this.router.navigate(['student', corso.name.toLowerCase().replace(/\s+/g, '-'), 'groups']).then();
    this.studentService.findProfessorsByCourse(this.singoloCorso.name).subscribe(professor => {
        this.professors = professor;
        this.professorsList = professor.map((p) => p.name + ' ' + p.firstName).join(', ');
      },
      error => {
        this.genericError();
      });
    // BehaviorSubject utilizzato per notificare il cambio di un corso nella sidenav
    this.studentService.eventsSubjectChangeCorsoSideNav.next();
  }

  genericError() {
    this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
      duration: 5000
    });
    location.reload();
  }
}
