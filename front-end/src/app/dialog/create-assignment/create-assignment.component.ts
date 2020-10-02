import {Component, OnInit} from '@angular/core';
import {FormGroup, NgForm} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {MatDatepickerInputEvent} from '@angular/material/datepicker';
import {SubmissionModel} from "../../models/submission.model";
import {ProfessorService} from "../../services/professor.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {CourseModel} from '../../models/course.model';
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
  content: string;
  chosenReleaseDate: Date;
  chosenExpiryDate: Date;

  canChoseExpiryDate = false;
  errorExpiryDate = false;
  file: File;

  private courseParam: string;
  private corso: CourseModel;
  filename: string = "Choose file";

  constructor(private dialogRef: MatDialogRef<CreateAssignmentComponent>, private professorService: ProfessorService,
              private snackBar: MatSnackBar, private router: Router) {
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
      submission.content = this.file.name;

      // close  dialog
      this.professorService.createAssignment(this.corso.name, submission, this.file).subscribe(
        (res) => {
          // @Todo -> fare la richiesta per avere anche l'assignment appena inserito
          this.dialogRef.close();
          this.snackBar.open('Assignment created successfully.', 'OK', {
            duration: 5000
          });
        },
        (error) => {
          this.dialogRef.close();
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

  handleFileSelect($event: any) {
    this.file = $event.target.files[0];
    if (this.file !== undefined) {
      this.filename = this.file.name;
    } else {
      this.filename = 'Choose file';
    }
  }

}
