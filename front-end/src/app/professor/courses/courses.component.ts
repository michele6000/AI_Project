import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {CreateCourseComponent} from '../../dialog/create-course/create-course.component';
import {CourseModel} from '../../models/course.model';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class CoursesComponent implements OnInit {
  url: any;
  columns = ['identifier', 'name', 'min', 'max'];
  data: CourseModel[] = [
    {
      identifier: 'AI',
      name: 'Applicazioni Internet',
      min: 3,
      max: 4
    },
    {
      identifier: 'BD',
      name: 'Big Data',
      min: 2,
      max: 4
    }
  ];

  constructor(private dialog: MatDialog, private router: Router, private activeRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activeRoute.queryParamMap
      .subscribe(params => {
        if (params.has('doCreate') && params.get('doCreate') === 'true') {
          this.dialog.open(CreateCourseComponent, {disableClose: true})
            .afterClosed()              // dopo la chiusura del dialog faccio le redirect
            .subscribe(result => {
              if (result) {              // se result è false => click su CANCEL -> redirect a HOME
                this.router.navigate(['/teacher/courses']);
              } else {
                this.router.navigate(['/teacher/courses']);
              }
            });
        }
      });
    if (localStorage.getItem('url_teacher')) {
      this.url = localStorage.getItem('url_teacher');
    } else {
      this.url = '/teacher/';
    }
  }

  deleteCourse($event: CourseModel[]) {
    console.log($event);
  }
}
