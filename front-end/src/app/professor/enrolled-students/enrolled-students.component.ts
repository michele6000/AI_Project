import {Component, OnInit, ViewChild} from '@angular/core';
import {switchMap} from 'rxjs/operators';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {CourseModel} from '../../models/course.model';
import {StudentModel} from '../../models/student.model';
import {MatTable} from '@angular/material/table';

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

  constructor(private route: ActivatedRoute, private router: Router) {
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
}
