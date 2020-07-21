import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {CourseModel} from '../models/course.model';
import {ActivatedRoute, Router} from '@angular/router';
import {CrudService} from "../services/crud.service";

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

  constructor(private route: ActivatedRoute, private router: Router, private crudService: CrudService) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(param => {
      const courseName = param.get('course');
      const course = this.corsi.filter(c => c.name.toLowerCase().replace(' ', '-') === courseName);
      if (course.length > 0) {
        this.changeCorso(course[0]);
      } else {
        this.router.navigate(['student', this.corsi[0].name.toLowerCase().replace(' ', '-')]);
      }
    });
  }

  toggleMenu() {
    this.sidenav.toggle();
  }

  changeCorso(corso: CourseModel) {
    this.singoloCorso = corso;
    this.router.navigate(['student', corso.name.toLowerCase().replace(' ', '-')]).then();
  }
}
