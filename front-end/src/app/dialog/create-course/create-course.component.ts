import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {CourseModel} from '../../models/course.model';
import {MatDialogRef} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-create-course',
  templateUrl: './create-course.component.html',
  styleUrls: ['./create-course.component.css']
})
export class CreateCourseComponent implements OnInit {
  error = false;

  constructor(private professorService: ProfessorService, private dialogRef: MatDialogRef<CreateCourseComponent>, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
  }

  createCourse(f: NgForm) {
    const course: CourseModel = {
      name: f.value.name,
      acronymous: f.value.identifier,
      min: f.value.min,
      max: f.value.max,
      enabled: true
    };
    if (this.professorService.createCourse(course)){
      this.dialogRef.close();
      this.snackBar.open('Course create successfully', 'OK', {
        duration: 5000
      });
    } else {
      this.snackBar.open('Error creating course ' + course.name, 'OK', {
        duration: 5000
      });
    }
  }
}
