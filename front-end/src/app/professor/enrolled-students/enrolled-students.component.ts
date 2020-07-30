import {Component, OnInit, ViewChild} from '@angular/core';
import {concatMap, switchMap, toArray} from 'rxjs/operators';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {from, Observable} from 'rxjs';
import {CourseModel} from '../../models/course.model';
import {StudentModel} from '../../models/student.model';
import {MatTable} from '@angular/material/table';
import {HttpClient} from '@angular/common/http';
import {CrudService} from "../../services/crud.service";
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

  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private crudService: CrudService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    this.corso = this.crudService.findCourseByNameUrl(this.courseParam);

    this.crudService.getEnrolledStudents(this.corso.name).subscribe(
      (res) => {
        this.data = res;
      }
    );

    this.crudService.getStudents().subscribe(
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
        return this.crudService.deleteStudent(this.corso.name, s.id);
      }),
      toArray()
    );

    res.subscribe((result: boolean[]) => {
      this.crudService.getEnrolledStudents(this.corso.name).subscribe((students) => this.data = students);
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
    this.crudService.enrollStudent(this.corso.name, $event.id).subscribe((res) => {
      if (res) {
        this.crudService.getEnrolledStudents(this.corso.name).subscribe((students) => this.data = students);
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
    formData.append('uploadFile', this.file, this.file.name);
    const headers = {
      'Content-Type': 'multipart/form-data'
    };
    this.http.post('http://localhost:4200/api/file', formData, {headers})
      .subscribe(
        data => console.log('success'),
        error => console.log(error)
      );
  }
}
