import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CourseModel} from '../models/course.model';
import {VmModel} from '../models/vm.model';
import {VmType} from '../models/vm-type.model';
import {BehaviorSubject, Observable} from 'rxjs';
import {StudentModel} from '../models/student.model';
import {GroupModel} from '../models/group.model';
import {SubmissionModel} from '../models/submission.model';
import {SolutionModel} from '../models/solution.model';
import {ProfessorModel} from '../models/professor.model';

const API_URL = '/api/API/';

@Injectable({
  providedIn: 'root'
})
export class ProfessorService {
  // private dataStore: {courses: CourseModel[]} = {courses: null};
  eventsSubjectChangeCorsoSideNav: BehaviorSubject<void> = new BehaviorSubject<void>(null);
  courses: Observable<CourseModel[]>;
  students: Observable<StudentModel[]>;
  private coursesSubject: BehaviorSubject<CourseModel[]>;
  private studentsSubject: BehaviorSubject<StudentModel[]>;

  constructor(private http: HttpClient) {
    this.coursesSubject = new BehaviorSubject<CourseModel[]>(null);
    this.courses = this.coursesSubject.asObservable();
    this.studentsSubject = new BehaviorSubject<StudentModel[]>(null);
    this.students = this.studentsSubject.asObservable();
  }

  /* COURSES */
  createCourse(course: CourseModel) {
    return this.http.post(
      API_URL + 'courses',
      course
    );
  }

  addProfessorToCourse(courseName: string, teacherMatricola: string) {
    return this.http.post(
      API_URL + 'courses/' + courseName + '/addProfessor?id=' + teacherMatricola, {}
    );
  }

  findCourseByIdentifier(courseIdentifier: string): Observable<CourseModel> {
    return this.http.get<CourseModel>(API_URL + 'course/' + courseIdentifier)
      .pipe(
        response => response
      );
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
      .filter((c) => c.name.replace(/\s+/g, '-').toLowerCase() === nameUrl);
    if (filteredCourses.length > 0) {
      return filteredCourses[0];
    } else {
      return {acronymous: '', max: 0, min: 0, name: '', enabled: false};
    }
  }

  enableCourse(courseName: string) {
    return this.http.post(
      API_URL + 'courses/' + courseName + '/enable', {}
    );
  }

  disableCourse(courseName: string) {
    return this.http.post(
      API_URL + 'courses/' + courseName + '/disable', {}
    );
  }

  /* VMs */
  createVMType(courseName: string, vm: VmType) {
    return this.http.post<string>(API_URL + 'courses/' + courseName + '/createVMType', vm);
  }

  setVMType(courseName: string, vmtId: string) {
    return this.http.post(API_URL + 'courses/' + courseName + '/setVMType?vmtId=' + vmtId, {});
  }

  findVmsByTeam(teamId: number) {
    return this.http.get<VmModel[]>(API_URL + 'team/' + teamId + '/vms');
  }

  findVmTypeByCourse(courseName: string) {
    return this.http.get<VmModel>(API_URL + 'courses/' + courseName + '/getVMType');
  }

  /* STUDENTS */
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

  deleteStudent(courseName: string, studentId: string) {
    if (courseName.length > 0 && studentId.length > 0) {
      return this.http.post(API_URL + 'courses/' + courseName + '/deleteOne?studentId=' + studentId, {});
    }
  }

  getEnrolledStudents(courseName: string): Observable<StudentModel[]> {
    if (courseName.length > 0) {
      return this.http.get<StudentModel[]>(API_URL + 'courses/' + courseName + '/enrolled');
    }
  }

  enrollStudent(courseName: string, studentId: string) {
    if (courseName.length > 0 && studentId.length > 0) {
      return this.http.post(API_URL + 'courses/' + courseName + '/enrollOne', {id: studentId});
    }
  }

  /* TEAMS */
  findTeamsByCourse(courseName: string) {
    if (courseName.length > 0) {
      return this.http.get<GroupModel[]>(API_URL + 'courses/' + courseName + '/teams');
    }
  }

  setTeamLimits(teamId: number, team: GroupModel) {
    return this.http.post<any>(API_URL + 'team/' + teamId + '/setTeamLimits', team);
  }

  /* SUBMISSIONS */
  createAssignment(courseName: string, submission: SubmissionModel, file: File) {
    const formData = new FormData();
    const submissionStr = new Blob([JSON.stringify(submission)], {type: 'application/json'});
    formData.append('submission', submissionStr);
    /*formData.append('submission.submissionDTO.content', submission.content);
    formData.append('submission.submissionDTO.expiryDate', moment(submission.expiryDate).format('YYYY-MM-DD HH:mm:ss'));
    formData.append('submission.submissionDTO.releaseDate', moment(submission.releaseDate).format('YYYY-MM-DD HH:mm:ss'));*/
    formData.append('file', file);
    return this.http.post(API_URL + 'courses/' + courseName + '/addSubmission', formData);
    /*return this.http.post(API_URL + 'courses/' + courseName + '/addSubmission', {
      submission: {
        multipartFile: file,
        submissionDTO: submission
      }
    });*/
  }

  findAssignmentsByCourse(courseName: string) {
    if (courseName.length > 0) {
      return this.http.get<any[]>(API_URL + 'courses/' + courseName + '/getAllSubmissions');
    }
  }

  getLatestSolution(studentId: string, submissionId: number) {
    if (studentId.length > 0) {

      return this.http.get<SolutionModel>(API_URL + 'students/' + studentId + '/' + submissionId + '/getLatestSolution');
    }
  }

  stopRevisions(solutionId: number) {
    return this.http.post<any>(API_URL + 'courses/' + solutionId + '/stopRevisions', {});
  }

  evaluateSolution(studentId: string, solutionId: number, evaluation: number, file: File) {
    const formData = new FormData();
    const evaluationStr = new Blob([JSON.stringify(evaluation)], {type: 'application/json'});
    // se lo mando come number me lo sottolinea di rosso -> @TODO: Testare entrambi i casi
    formData.append('message', 'Mark');
    formData.append('mark', evaluationStr);
    formData.append('file', file);
    return this.http.post<any>(API_URL + 'students/' + studentId + '/' + solutionId + '/addCorrection', formData);
  }

  reviewSolution(studentId: string, solutionId: number, review: string, file: File) {
    const formData = new FormData();
    // const reviewStr = new Blob([JSON.stringify(review)], { type: 'application/json'});
    formData.append('message', review);
    formData.append('mark', '0');
    formData.append('file', file);
    return this.http.post<any>(API_URL + 'students/' + studentId + '/' + solutionId + '/addCorrection', formData);
  }

  updateCourse(course: CourseModel) {
    return this.http.post<boolean>(API_URL + 'courses/' + course.name + '/update', course);
  }

  deleteCourse(courseName: string) {
    if (courseName.length > 0) {
      return this.http.post<boolean>(API_URL + 'courses/' + courseName + '/delete', {});
    }
  }

  findMembersByTeamId(teamId: number) {
    return this.http.get<StudentModel[]>(API_URL + 'team/' + teamId + '/members');
  }

  findAllProfessor() {
    return this.http.get<ProfessorModel[]>(API_URL + 'professor/getAll');
  }

  findAllProsessorByCourse(courseName: string) {
    return this.http.get<ProfessorModel[]>(API_URL + 'courses/' + courseName + '/professors');
  }

  deleteProfessorFromCourse(courseName: string, professorId: string) {
    return this.http.post(API_URL + 'courses/' + courseName + '/deleteProfessor?id=' + professorId, {});
  }

  findStatisticsByTeam(teamId: number) {
    return this.http.get<any>(API_URL + 'team/' + teamId + '/stats');
  }

  deleteTeam(teamId: number) {
    return this.http.post(API_URL + 'team/' + teamId + '/evictTeam', {});
  }

  deleteStudentFromTeam(teamId: number, studentId: string) {
    return this.http.post(API_URL + 'team/' + teamId + '/' + studentId + '/deleteMember', {});
  }

  addStudentToTeam(teamId: number, studentId: string) {
    return this.http.post(API_URL + 'team/' + teamId + '/' + studentId + '/addMember', {});
  }

}
