import {Component, OnInit, ViewChild} from '@angular/core';
import {switchMap} from 'rxjs/operators';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {CourseModel} from '../../models/course.model';
import {StudentModel} from '../../models/student.model';
import {MatTable} from '@angular/material/table';
import {HttpClient} from '@angular/common/http';
import {CrudService} from "../../services/crud.service";

@Component({
  selector: 'app-enrolled-students',
  templateUrl: './enrolled-students.component.html',
  styleUrls: ['./enrolled-students.component.css']
})
export class EnrolledStudentsComponent implements OnInit {

  @ViewChild(MatTable)
  table: MatTable<StudentModel>;

  corso: CourseModel;
  columns = ['email', 'firstName', 'name', 'id'];
  data: StudentModel[] = [];
  fileAbsent = true;
  file: any;
  courseParam: string;
  students: StudentModel[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private crudService: CrudService) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    this.corso = this.crudService.findCourseByNameUrl(this.courseParam);

    this.crudService.getEnrolledStudents(this.corso.name).subscribe(
      (res) => this.data = res
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
    console.log($event);
  }

  addStudent($event: StudentModel) {
    this.crudService.enrollStudent($event).subscribe((res) => {
      if (res) {
        this.crudService.getStudents(true);
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
