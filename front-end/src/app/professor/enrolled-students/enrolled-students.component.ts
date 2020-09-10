import {Component, OnInit, ViewChild} from '@angular/core';
import {concatMap, switchMap, toArray} from 'rxjs/operators';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {from, Observable} from 'rxjs';
import {CourseModel} from '../../models/course.model';
import {StudentModel} from '../../models/student.model';
import {MatTable} from '@angular/material/table';
import {HttpClient} from '@angular/common/http';
import {ProfessorService} from "../../services/professor.service";
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-enrolled-students',
  templateUrl: './enrolled-students.component.html',
  styleUrls: ['./enrolled-students.component.css']
})
export class EnrolledStudentsComponent implements OnInit {


  corso: CourseModel;
  columns = ['email', 'firstName', 'name', 'id'];
  data: StudentModel[] = [];
  fileAbsent = true;
  file: any;
  courseParam: string;
  students: StudentModel[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private professorService: ProfessorService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    this.corso = this.professorService.findCourseByNameUrl(this.courseParam);

    this.professorService.getEnrolledStudents(this.corso.name).subscribe(
      (res) => {
        this.data = res;
      }
    );

    this.professorService.getStudents().subscribe(
      (students) => {
        if (students) {
          this.students = students;
        } else {
          this.students = [];
        }
      }
    );
    /*
    this.route.paramMap.pipe(
      switchMap((params: ParamMap) =>
        this.course = params.get('course')
      )
    );
    console.log(this.course);
     */
  }

  deleteStudent($event: StudentModel[]) {
    const res = from($event).pipe(
      concatMap(s => {
        return this.professorService.deleteStudent(this.corso.name, s.id);
      }),
      toArray()
    );

    res.subscribe((result: boolean[]) => {
      this.professorService.getEnrolledStudents(this.corso.name).subscribe((students) => this.data = students);
      if (result.filter(e => !e).length > 0) {
        // Almeno una ha fallito
      } else {
        // Tutte a buon fine
        this.snackBar.open('Students deleted successfully.', 'OK', {
          duration: 5000
        });
      }
    });
  }

  addStudent($event: StudentModel) {
    this.professorService.enrollStudent(this.corso.name, $event.id).subscribe((res) => {
      if (res) {
        this.professorService.getEnrolledStudents(this.corso.name).subscribe((students) => this.data = students);

        this.snackBar.open('Student added successfully.', 'OK', {
          duration: 5000
        });
      }
    });
  }

  handleFileSelect($event: any) {
    console.log($event);
    this.file = $event.target.files[0];
    this.fileAbsent = false;
  }

  sendFile() {
    const formData: FormData = new FormData();
    formData.append('file', new Blob([this.file], {type: 'text/csv'}), this.file.name);
    const headers = {
      'Content-Type': 'multipart/form-data'
    };
    this.http.post('/api/API/courses/' + this.corso.name + '/enrollMany', formData)
      .subscribe(
        (result: boolean[]) => {
          if (result.filter((r) => !r).length > 0) {
            // Almeno un caricamento fallito
            this.snackBar.open('Error while uploading ' + result.filter((r) => !r).length + ' student(s). Try again.', 'OK', {
              duration: 5000
            });
          } else {
            this.snackBar.open(result.length + ' students added successfully.', 'OK', {
              duration: 5000
            });
          }
        },
        error => {
          this.snackBar.open('Error while uploading student list. Incorrect format.', 'OK', {
            duration: 5000
          });
        }
      );
  }
}
