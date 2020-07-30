import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {CreateCourseComponent} from '../../dialog/create-course/create-course.component';
import {CourseModel} from '../../models/course.model';
import {EditCourseComponent} from '../../dialog/edit-course/edit-course.component';
import {CrudService} from "../../services/crud.service";
import {AuthService} from "../../auth/auth.service";

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class CoursesComponent implements OnInit {
  url: any;
  columns = ['acronymous', 'name', 'min', 'max'];
  data: CourseModel[] = [];

  constructor(private dialog: MatDialog, private router: Router, private activeRoute: ActivatedRoute, private crudService: CrudService, private authService: AuthService) {

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

    this.authService.user.subscribe((user) => {
      if (user != null) {
        this.crudService.courses.subscribe(
          (courses) => {
            if (courses) {
              this.data = courses;
            } else {
              this.data = [];
            }
          }
        );
      }
    });
  }

  deleteCourse($event: CourseModel[]) {
    console.log($event);
  }

  editCourse(course: CourseModel) {
    this.dialog.open(EditCourseComponent, {data: course})
      .afterClosed()
      .subscribe(result => {

      });
  }

  changeActive($event: CourseModel) {
    if ($event.enabled) {
      this.crudService.disableCourse($event.name).subscribe(result => {

      });
    } else {
      this.crudService.enableCourse($event.name).subscribe(result => {

      });
    }
  }
}
