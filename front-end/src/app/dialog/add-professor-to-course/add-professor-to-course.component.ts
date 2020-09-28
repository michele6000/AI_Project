import {Component, Inject, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ProfessorModel} from '../../models/professor.model';

@Component({
  selector: 'app-add-professor-to-course',
  templateUrl: './add-professor-to-course.component.html',
  styleUrls: ['./add-professor-to-course.component.css']
})
export class AddProfessorToCourseComponent implements OnInit {
  error: boolean = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: ProfessorModel,private dialogRef: MatDialogRef<AddProfessorToCourseComponent>) { }

  ngOnInit(): void {
  }

  addProfessorToCourse(f: NgForm) {

  }

  closeDialog() {
    this.dialogRef.close(false);
  }
}
