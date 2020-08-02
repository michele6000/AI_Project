import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {CourseModel} from '../models/course.model';
import {HttpClient} from '@angular/common/http';
import {VmStudent} from '../models/vm-student.model';
import {StudentModel} from "../models/student.model";
import {GroupModel} from "../models/group.model";

const API_URL = '/api/API/';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  courses: Observable<CourseModel[]>;
  private coursesSubject: BehaviorSubject<CourseModel[]>;

  teams: Observable<GroupModel[]>;
  private teamsSubject: BehaviorSubject<GroupModel[]>;

  constructor(private http: HttpClient) {
    this.coursesSubject = new BehaviorSubject<CourseModel[]>(null);
    this.courses = this.coursesSubject.asObservable();

    this.teamsSubject = new BehaviorSubject<GroupModel[]>(null);
    this.teams = this.teamsSubject.asObservable();
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

  findTeamsByStudent(studentId: string, refresh = false) {
    if (this.teamsSubject.value !== undefined || refresh) {
      return this.http.get<GroupModel[]>(API_URL + 'students/' + studentId + '/teams')
        .subscribe(response => {
          this.teamsSubject.next(response);
        });
    } else {
      this.teamsSubject.next(this.teamsSubject.value);
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

  proposeTeam(students: StudentModel[], courseName: string, groupName: string) {
    const studentIds = students.map(s => s.id);
    studentIds.push(localStorage.getItem('id'));

    return this.http.post(
      API_URL + 'courses/' + courseName + '/proposeTeam?name=' + groupName, studentIds
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

  findAvailableStudentsByCourseName(courseName: string) {
    return this.http.get<StudentModel[]>(
      API_URL + 'courses/' + courseName + '/availableStudents'
    );
  }

  findMembersByTeamId(teamId: number) {
    return this.http.get<any[]>(API_URL + 'team/' + teamId + '/members');
  }
}
