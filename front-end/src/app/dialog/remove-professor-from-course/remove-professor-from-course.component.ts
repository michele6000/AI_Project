import {Component, Inject, OnInit} from '@angular/core';
import {ProfessorService} from '../../services/professor.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ProfessorModel} from '../../models/professor.model';
import {from} from 'rxjs';
import {concatMap, toArray} from 'rxjs/operators';

@Component({
  selector: 'app-remove-professor-from-course',
  templateUrl: './remove-professor-from-course.component.html',
  styleUrls: ['./remove-professor-from-course.component.css']
})
export class RemoveProfessorFromCourseComponent implements OnInit {
  error: boolean = false;
  professors: ProfessorModel[];
  courseName: string;
  columns = ['id', 'name', 'firstName'];


  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private dialogRef: MatDialogRef<RemoveProfessorFromCourseComponent>,
              private professorService: ProfessorService, private snackBar: MatSnackBar) {
    this.professors = data.professors;
    this.courseName = data.courseName;
  }

  ngOnInit(): void {
  }

  deleteProfessor(professors: ProfessorModel[]) {
    // Controllo che non siano selezionati tutti i professori
    if (professors.length === this.professors.length) {
      this.snackBar.open('You cannot remove all professors from a course.', 'OK', {
        duration: 5000
      });
      return;
    }

    const res = from(professors).pipe(
      concatMap(professor => {
        return this.professorService.deleteProfessorFromCourse(this.courseName, professor.id);
      }),
      toArray()
    );

    res.subscribe((result: boolean[]) => {
        if (result.filter(e => !e).length > 0) {
          // Almeno una ha fallito
          this.dialogRef.close(true);
          this.snackBar.open('Error delete professors .', 'OK', {
            duration: 5000
          });
        } else {
          // Tutte a buon fine
          this.dialogRef.close(true);
          this.snackBar.open('Professor removed succesfully.', 'OK', {
            duration: 5000
          });
        }
      },
      error => {
        this.genericError();
      });
  }

  genericError() {
    this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
      duration: 5000
    });
    location.reload();
  }
}
