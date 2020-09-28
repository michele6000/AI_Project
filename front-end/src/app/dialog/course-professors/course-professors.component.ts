import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ProfessorModel} from '../../models/professor.model';

@Component({
  selector: 'app-course-professors',
  templateUrl: './course-professors.component.html',
  styleUrls: ['./course-professors.component.css']
})
export class CourseProfessorsComponent implements OnInit {

  professors: ProfessorModel[] = [];
  courseName: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data, private dialogRef: MatDialogRef<CourseProfessorsComponent>) {
    this.professors = data.professors;
    this.courseName = data.courseName;
  }

  ngOnInit(): void {
  }

  closeDialog() {
    this.dialogRef.close(false);
  }

}
