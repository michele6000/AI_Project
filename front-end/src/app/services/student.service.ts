import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {CourseModel} from '../models/course.model';
import {HttpClient} from '@angular/common/http';
import {VmStudent} from "../models/vm-student.model";

const API_URL = '/api/API/';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  courses: Observable<CourseModel[]>;
  private coursesSubject: BehaviorSubject<CourseModel[]>;

  constructor(private http: HttpClient) {
    this.coursesSubject = new BehaviorSubject<CourseModel[]>(null);
    this.courses = this.coursesSubject.asObservable();
  }

  findCoursesByStudent(studentId: string, refresh = false) {
    if (this.coursesSubject.value !== undefined || refresh) {
      return this.http.get<CourseModel[]>(API_URL + 'students/' + studentId + '/courses')
        .subscribe(response => {
          this.coursesSubject.next(response);
        });
    } else {
      this.coursesSubject.next(this.coursesSubject.value);
    }
  }

  // Cerca nell'array locale di corsi il corso dato il path nell'URL
  findCourseByNameUrl(nameUrl: string): CourseModel {
    const filteredCourses = this.coursesSubject.value
      .filter((c) => c.name.replace(' ', '-').toLowerCase() === nameUrl);
    if (filteredCourses.length > 0) {
      return filteredCourses[0];
    } else {
      return {acronymous: '', max: 0, min: 0, name: '', enabled: false};
    }
  }

  proposeGroup(group: any) {
    this.http.post(
      API_URL + 'student/propose-group',
      {
        group
      }
    ).subscribe(
      (payload: any) => {

      },
      (error: any) => {

      }
    );
  }

  findGroupByStudentId(studentId: string): Observable<any> {
    return this.http.get<any>(API_URL + 'student/' + studentId)
      .pipe(
        response => response
      );
  }

  createVM(vm: VmStudent) {
    this.http.post(
      API_URL,
      {
        vm
      }
    ).subscribe(
      (payload: any) => {

      },
      (error: any) => {

      }
    );
  }
}
