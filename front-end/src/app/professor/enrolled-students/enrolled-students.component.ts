import {Component, OnInit, ViewChild} from '@angular/core';
import {switchMap} from 'rxjs/operators';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {CourseModel} from '../../models/course.model';
import {StudentModel} from '../../models/student.model';
import {MatTable} from '@angular/material/table';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-enrolled-students',
  templateUrl: './enrolled-students.component.html',
  styleUrls: ['./enrolled-students.component.css']
})
export class EnrolledStudentsComponent implements OnInit {

  @ViewChild(MatTable)
  table: MatTable<StudentModel>;

  corso: CourseModel;
  columns = ['email', 'name', 'surname', 'matricola'];
  data: StudentModel[] = [
    {
      email: 's123456',
      name: 'Mario',
      surname: 'Rossi',
      matricola: '123456'
    },
    {
      email: 's123456',
      name: 'Paolo',
      surname: 'Verdi',
      matricola: '123456'
    }
  ];
  fileAbsent = true;
  file: any;

  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient) {
  }

  ngOnInit(): void {
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
    console.log($event);
    // this.table.renderRows();
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
