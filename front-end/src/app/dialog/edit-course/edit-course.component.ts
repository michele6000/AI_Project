import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {CourseModel} from '../../models/course.model';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-course',
  templateUrl: './edit-course.component.html',
  styleUrls: ['./edit-course.component.css']
})
export class EditCourseComponent implements OnInit {
  error = false;
  course: CourseModel;

  constructor(@Inject(MAT_DIALOG_DATA) public data: CourseModel, private professorService: ProfessorService,
              private dialogRef: MatDialogRef<EditCourseComponent>, private snackBar: MatSnackBar) {
    this.course = data;
  }

  ngOnInit(): void {

  }

  updateCourse(f: NgForm) {
    this.course.name = f.value.name;
    this.course.acronymous = f.value.identifier;
    this.course.min = f.value.min;
    this.course.max = f.value.max;

    this.professorService.updateCourse(this.course).subscribe(
      res => {
        if (res) {
          this.dialogRef.close(true);
          this.snackBar.open('Course update successfully', 'OK', {
            duration: 5000
          });
        } else {
          this.dialogRef.close(false);
          this.snackBar.open('Error updating course', 'OK', {
            duration: 5000
          });
        }
      }
    );
  }
}
