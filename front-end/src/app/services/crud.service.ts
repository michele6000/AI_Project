import { Injectable } from '@angular/core';
import {UserModel} from '../models/user.models';
import {HttpClient} from '@angular/common/http';
import {CourseModel} from '../models/course.model';
import {VmModel} from '../models/vm.model';
import {VmProfessor} from '../models/vm-professor.model';
import {VmStudent} from '../models/vm-student.model';
import {Observable} from 'rxjs';

const API_URL = 'http://localhost:3000/';

@Injectable({
  providedIn: 'root'
})
export class CrudService {

  private dataStore: {courses: CourseModel[]} = {courses: [
      {name: 'Applicazioni Internet', identifier: 'AI', min: 2, max: 4},
      {name: 'Big Data', identifier: 'BD', min: 3, max: 4}
    ]};

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

  createVMTeacher(vm: VmProfessor) {
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

  createVMStudent(vm: VmStudent) {
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

  findGroupByStudentId(studentId: string): Observable<any>{
    return this.http.get<any>(API_URL + 'student/' + studentId)
      .pipe(
        response => response
      );
  }

  proposeGroup(group: any){
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

  findCourseByIdentifier(courseIdentifier: string): Observable<CourseModel>{
    return this.http.get<CourseModel>(API_URL + 'course/' + courseIdentifier)
      .pipe(
        response => response
      );
  }

  findCoursesByStudent(studentId: string): Observable<CourseModel[]>{
    return this.http.get<CourseModel[]>(API_URL + 'student/' + studentId + '/courses')
      .pipe(response => {
        // @todo Migliorare
        response.subscribe(courses => this.dataStore.courses = courses);
        return response;
      });
  }

  // Cerca nell'array locale di corsi il corso dato il path nell'URL
  findCourseByNameUrl(nameUrl: string): CourseModel {
    const filteredCourses = this.dataStore.courses
      .filter((c) => c.name.replace(' ', '-').toLowerCase() === nameUrl);
    if (filteredCourses.length > 0) {
      return filteredCourses[0];
    }
    else { return {identifier: '', max: 0, min: 0, name: ''}; }
  }

}
