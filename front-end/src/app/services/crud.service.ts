import { Injectable } from '@angular/core';
import {UserModel} from '../models/user.models';
import {HttpClient} from '@angular/common/http';
import {CourseModel} from '../models/course.model';

const API_URL = 'http://localhost:3000/';

@Injectable({
  providedIn: 'root'
})
export class CrudService {

  constructor(private http: HttpClient) { }

  createCourse(course: CourseModel) {
    this.http.post(
      API_URL,
      {
        course
      }
    ).subscribe(
      (payload: any) => {

      },
      (error: any) => {

      }
    );
  }
}
