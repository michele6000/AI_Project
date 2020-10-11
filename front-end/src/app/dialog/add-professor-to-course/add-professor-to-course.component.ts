import {Component, Inject, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ProfessorModel} from '../../models/professor.model';
import {ProfessorService} from '../../services/professor.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-professor-to-course',
  templateUrl: './add-professor-to-course.component.html',
  styleUrls: ['./add-professor-to-course.component.css']
})
export class AddProfessorToCourseComponent implements OnInit {
  error: boolean = false;
  errorAddProfessor: boolean = false;
  professors: ProfessorModel[];
  courseName: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private dialogRef: MatDialogRef<AddProfessorToCourseComponent>,
              private professorService: ProfessorService, private snackBar: MatSnackBar) {
    this.professors = data.professors;
    this.courseName = data.courseName;
  }

  ngOnInit(): void {
  }

  addProfessorToCourse(f: NgForm) {
    const professorId = f.value.professor;
    this.professorService.addProfessorToCourse(this.courseName, professorId).subscribe(
      res => {
        this.dialogRef.close(true);
        this.errorAddProfessor = false;
        this.snackBar.open('Professor added successfully to course ' + this.courseName, 'OK', {
          duration: 5000
        });
      },
      error => {
        this.errorAddProfessor = true;
      }
    );
  }
}
