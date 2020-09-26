import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {CourseModel} from '../models/course.model';
import {ActivatedRoute, Router} from '@angular/router';
import {StudentService} from "../services/student.service";
import {Subject, Subscription} from 'rxjs';

@Component({
  selector: 'app-student',
  templateUrl: './student.component.html',
  styleUrls: ['./student.component.css']
})
export class StudentComponent implements OnInit {

  corsi: CourseModel[] = [];

  singoloCorso: CourseModel;

  @ViewChild(MatSidenav)
  sidenav: MatSidenav;

  s1: Subscription;

  constructor(private route: ActivatedRoute, private router: Router, private studentService: StudentService) {
    this.s1 = this.studentService.courses.subscribe((next) => {
      if (next) {
        this.corsi = next;
        this.checkUrl();
      } else {
        this.corsi = [];
      }
    });
  }

  ngOnInit(): void {
  }

  checkUrl() {
    this.route.paramMap.subscribe(param => {
      const courseName = param.get('course');
      const course = this.corsi.filter(c => c.name.toLowerCase().replace(' ', '-') === courseName);
      if (course.length > 0) {
        this.changeCorso(course[0]);
      } else if (this.corsi.length > 0) {
        this.router.navigate(['student', this.corsi[0].name.toLowerCase().replace(' ', '-')]);
      } else {
        console.log('Nessun corso!');
      }
    });
  }

  toggleMenu() {
    this.sidenav.toggle();
  }

  changeCorso(corso: CourseModel) {
    this.singoloCorso = corso;
    this.router.navigate(['student', corso.name.toLowerCase().replace(' ', '-'), 'groups']).then();
    // BehaviorSubject utilizzato per notificare il cambio di un corso nella sidenav
    this.studentService.eventsSubjectChangeCorsoSindeNav.next();
  }
}
