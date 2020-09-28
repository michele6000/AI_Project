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
  errorStr: string;

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

    this.errorStr = '';
    this.error = false;

    // Creo il corso
    this.professorService.createCourse(course).subscribe((res) => {

      // Aggiungo il professore al corso
      if (localStorage.getItem('id')) {
        this.professorService.addProfessorToCourse(course.name, localStorage.getItem('id')).subscribe((resAddProfessor) => {

          this.dialogRef.close(true);
          this.snackBar.open('Course create successfully', 'OK', {
            duration: 5000
          });

        }, (errorAddProfessor) => {
          this.snackBar.open('Error adding yourself to course ' + course.name, 'OK', {
            duration: 5000
          });
        });
      } else {
        // @todo Redirect a pagina 500
        // Non ho nello storage l'ID dell'utente loggato
      }

    }, error => {
      this.error = true;
      this.errorStr = error.statusText + ' ' + error.error.message;
      this.snackBar.open('Error creating course ' + course.name, 'OK', {
        duration: 5000
      });
    });
  }
}
