import { Injectable } from '@angular/core';
import {UserModel} from '../models/user.models';
import {HttpClient} from '@angular/common/http';
import {CourseModel} from '../models/course.model';
import {VmModel} from "../models/vm.model";
import {VmProfessor} from "../models/vm-professor.model";
import {VmStudent} from "../models/vm-student.model";
import {Observable} from "rxjs";

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
    return this.http.get<any>(API_URL + "student/" + studentId)
      .pipe(
        response => response
      );
  }

  proposeGroup(group: any){
    this.http.post(
      API_URL + "student/propose-group",
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

}
