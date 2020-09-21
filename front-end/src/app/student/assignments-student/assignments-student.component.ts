import {Component, OnInit} from '@angular/core';
import {StudentService} from '../../services/student.service';
import {CourseModel} from '../../models/course.model';
import {Router} from '@angular/router';
import * as moment from 'moment';

@Component({
  selector: 'app-assignments-student',
  templateUrl: './assignments-student.component.html',
  styleUrls: ['./assignments-student.component.css']
})
export class AssignmentsStudentComponent implements OnInit {

  file: any;
  consegne = [];
  private courseParam: string;
  private corso: CourseModel;
  hasConsegne = false;

  imageToShow: any;

  createImageFromBlob(image: Blob) {
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      this.imageToShow = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

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
        if (consegne.length > 0) {
          this.hasConsegne = true;
        }
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
    console.log(this.file);
  }

  openSubmission(id: string) {
    this.studentService.getSubmissionById(this.corso.name, id).subscribe((res) => {
      console.log(res);
    });
  }

  uploadSolution(id: string) {
    // Carica la soluzione proposta dallo studente
    this.studentService.addSolution(localStorage.getItem('id'), id, this.file.name).subscribe((res) => {
      console.log(res);
    });
  }

  handleShowSubmission(id: string) {
    window.open("//"+"localhost:8080/API/courses/submissions/getImage/"+id, "_blank");
  }
}
