import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, NgForm} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {MatDatepickerInputEvent} from '@angular/material/datepicker';
import {SubmissionModel} from "../../models/submission.model";
import {ProfessorService} from "../../services/professor.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {CourseModel} from "../../models/course.model";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-assignment',
  templateUrl: './create-assignment.component.html',
  styleUrls: ['./create-assignment.component.css']
})
export class CreateAssignmentComponent implements OnInit {
  error = false;
  date: FormGroup;
  minDate: Date;
  minExpiryDate: Date;

  chosenReleaseDate: Date;
  chosenExpiryDate: Date;

  canChoseExpiryDate = false;
  errorExpiryDate = false;

  private courseParam: string;
  private corso: CourseModel;

  constructor(private dialogRef: MatDialogRef<CreateAssignmentComponent>, private professorService: ProfessorService, private snackBar: MatSnackBar, private router: Router) {

    this.minDate = new Date();
    this.minExpiryDate = new Date();
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    this.corso = this.professorService.findCourseByNameUrl(this.courseParam);
  }

  createAssignment(f: NgForm) {
    if (this.chosenExpiryDate > this.chosenReleaseDate) {
      const submission = new SubmissionModel();
      submission.releaseDate = this.chosenReleaseDate;
      submission.expiryDate = this.chosenExpiryDate;
      submission.content = 'LAB ' + (Math.random() * 10000);

      this.professorService.createAssignment(this.corso.name, submission).subscribe(
        (res) => {
          this.snackBar.open('Assignment created successfully.', 'OK', {
            duration: 5000
          });
        },
        (error) => {
          console.log(error);
          this.snackBar.open('Error creating assignment, try again.', 'OK', {
            duration: 5000
          });
        }
      );
    } else {
      // expiryDate è minore di releaseDate, mostro l'errore
      this.errorExpiryDate = true;
    }
  }

  closeDialog() {
    this.dialogRef.close(false);
  }

  expiryChoseValue($event: MatDatepickerInputEvent<any>) {
    this.chosenExpiryDate = $event.target.value;

    this.errorExpiryDate = false;
  }

  releaseChoseValue($event: MatDatepickerInputEvent<any>) {
    this.minExpiryDate = $event.target.value;
    this.chosenReleaseDate = $event.target.value;
    this.canChoseExpiryDate = true;

    this.errorExpiryDate = false;
  }

  handleFileSelect($event: Event) {

  }
}