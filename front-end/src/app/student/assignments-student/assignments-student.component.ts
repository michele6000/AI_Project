import {Component, OnInit} from '@angular/core';
import {StudentService} from "../../services/student.service";
import {CourseModel} from "../../models/course.model";
import {Router} from "@angular/router";
import * as moment from "moment";

@Component({
  selector: 'app-assignments-student',
  templateUrl: './assignments-student.component.html',
  styleUrls: ['./assignments-student.component.css']
})
export class AssignmentsStudentComponent implements OnInit {

  file: any;
  consegne = [
    {
      name: 'Lab 1',
      date: '06/07/2020',
      history: [
        {
          status: 'Rivisto',
          timestamp: '06/07/2020 14:57:12'
        },
        {
          status: 'Consegnato',
          timestamp: '06/07/2020 14:37:12'
        }
      ]
    }
  ];
  private courseParam: string;
  private corso: CourseModel;

  constructor(private studentService: StudentService, private router: Router) {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
    this.corso = this.studentService.findCourseByNameUrl(this.courseParam);

    this.studentService.findSubmissions(this.corso.name).subscribe(
      (res) => {
        const consegne = [];
        res.forEach((c) => {
          c.expiryString = moment(c.expiryDate).format('L');
          c.releaseString = moment(c.releaseDate).format('L');

          // @todo Riempire con tutti gli elaborati dello studente
          this.studentService.getHistorySolutions(localStorage.getItem('id'), c.id).subscribe(
            (resHistory) => {
              console.log(resHistory);
            },
            (errorHistory) => {

            }
          );
          c.history = [];
          consegne.push(c);
        });
        this.consegne = consegne;
      },
      (error) => {

      }
    );
  }

  ngOnInit(): void {
  }

  handleFileSelect($event: any) {
    console.log($event);
    this.file = $event.target.files[0];
  }
}
