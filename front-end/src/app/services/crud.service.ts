import {Injectable} from '@angular/core';
import {UserModel} from '../models/user.models';
import {HttpClient} from '@angular/common/http';
import {CourseModel} from '../models/course.model';
import {VmModel} from '../models/vm.model';
import {VmProfessor} from '../models/vm-professor.model';
import {VmStudent} from '../models/vm-student.model';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {StudentModel} from "../models/student.model";

const API_URL = '/api/API/';

@Injectable({
  providedIn: 'root'
})
export class CrudService {

  // private dataStore: {courses: CourseModel[]} = {courses: null};

  courses: Observable<CourseModel[]>;
  private coursesSubject: BehaviorSubject<CourseModel[]>;

  students: Observable<StudentModel[]>;
  private studentsSubject: BehaviorSubject<StudentModel[]>;

  constructor(private http: HttpClient) {
    this.coursesSubject = new BehaviorSubject<CourseModel[]>(null);
    this.courses = this.coursesSubject.asObservable();
    this.studentsSubject = new BehaviorSubject<StudentModel[]>(null);
    this.students = this.studentsSubject.asObservable();
  }

  createCourse(course: CourseModel) {
    return this.http.post(
      API_URL + 'courses',
      course
    ).subscribe(
      (payload: any) => {
        // @todo Remove after API change
        this.addProfessorToCourse(course.name, '1');
        // this.findCoursesByProfessor('1');
      },
      (error: any) => {

      }
    );
  }

  addProfessorToCourse(courseName: string, teacherMatricola: string) {
    return this.http.post(
      API_URL + 'courses/' + courseName + '/addProfessor?id=' + teacherMatricola, {}
    ).subscribe(
      (payload: any) => {
        this.findCoursesByProfessor('1');
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

  // Richiede l'elenco degli studenti al server se non ancora noti,
  //  altrimenti li recupera dalla variabile locale
  getStudents(refresh = false) {
    if (this.studentsSubject.value !== undefined || refresh) {
      this.http.get<StudentModel[]>(API_URL + 'students')
        .subscribe(response => {
          this.studentsSubject.next(response);
        });
    } else {
      this.studentsSubject.next(this.studentsSubject.value);
    }
    return this.students;
  }

  getEnrolledStudents(courseName: string): Observable<StudentModel[]> {
    return this.http.get<StudentModel[]>(API_URL + 'courses/' + courseName + '/enrolled');
  }

  findGroupByStudentId(studentId: string): Observable<any> {
    return this.http.get<any>(API_URL + 'student/' + studentId)
      .pipe(
        response => response
      );
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

  findCourseByIdentifier(courseIdentifier: string): Observable<CourseModel> {
    return this.http.get<CourseModel>(API_URL + 'course/' + courseIdentifier)
      .pipe(
        response => response
      );
  }

  findCoursesByStudent(studentId: string): Observable<CourseModel[]> {
    return this.http.get<CourseModel[]>(API_URL + 'student/' + studentId + '/courses')
      .pipe(response => {
        // @todo Migliorare
        response.subscribe(courses => this.coursesSubject.next(courses));
        return response;
      });
  }

  // Richiede l'elenco dei corsi al server se non ancora noti,
  //  altrimenti li recupera dalla variabile locale
  findCoursesByProfessor(professorId: string, refresh = false) {
    if (this.coursesSubject.value !== undefined || refresh) {
      return this.http.get<CourseModel[]>(API_URL + 'courses/' + professorId + '/getCourses')
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

  enableCourse(courseName: string){
    return this.http.post(
      API_URL + 'courses/' + courseName + '/enable', {}
    );
  }

  disableCourse(courseName: string){
    return this.http.post(
      API_URL + 'courses/' + courseName + '/disable', {}
    );
  }

  enrollStudent(courseName: string, studentModel: StudentModel){
    return this.http.post(
      API_URL + 'courses/' + courseName + '/enrollOne', {studentModel}
    );
  }

}
