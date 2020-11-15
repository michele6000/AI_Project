import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {StudentModel} from '../../models/student.model';
import {from} from 'rxjs';
import {concatMap, toArray} from 'rxjs/operators';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ProfessorService} from '../../services/professor.service';

@Component({
  selector: 'app-show-team-members',
  templateUrl: './show-team-members.component.html',
  styleUrls: ['./show-team-members.component.css']
})
export class ShowTeamMembersComponent implements OnInit {

  teamName: string;
  teamid: number;
  studentsInTeam: StudentModel[] = [];
  allStudentsOfCourse: StudentModel[] = [];
  columns = ['id', 'name', 'firstName'];

  constructor(@Inject(MAT_DIALOG_DATA) public data, private dialogRef: MatDialogRef<ShowTeamMembersComponent>,
              private professorService: ProfessorService, private snackBar: MatSnackBar) {
    this.teamName = data.teamName;
    this.teamid = data.teamId;
    this.studentsInTeam = data.students;
    this.allStudentsOfCourse = data.allStudentsOfCourse;
  }

  ngOnInit(): void {
  }

  deleteStudentFromTeam(students: StudentModel[]) {
    // Controllo che non siano selezionati tutti i professori
    if (students.length === this.studentsInTeam.length) {
      this.dialogRef.close(false);
      this.snackBar.open('You cannot remove all students from a team. You must evict the team', 'OK', {
        duration: 5000
      });
      return;
    }

    const res = from(students).pipe(
      concatMap(student => {
        return this.professorService.deleteStudentFromTeam(this.teamid, student.id);
      }),
      toArray()
    );

    res.subscribe((result: boolean[]) => {
        if (result.filter(e => !e).length > 0) {
          // Almeno una ha fallito
          this.dialogRef.close(true);
          this.snackBar.open('Error delete student from team.', 'OK', {
            duration: 5000
          });
        } else {
          // Tutte a buon fine
          this.dialogRef.close(true);
          this.snackBar.open('Student removed succesfully.', 'OK', {
            duration: 5000
          });
        }
      },
      error => {
        this.genericError();
      });
  }

  // @TODO verificare perchè il server risponde BAD REQUEST
  addStudentToTeam(student: StudentModel) {
    this.professorService.addStudentToTeam(this.teamid, student.id).subscribe((res) => {
      this.dialogRef.close(true);
      if (res){
        this.snackBar.open('Student added to team successfully.', 'OK', {
          duration: 5000
        });
      } else {
        this.snackBar.open('Error adding student to team.', 'OK', {
          duration: 5000
        });
      }
    }, (error) => {
      this.genericError();
    });
  }

  genericError() {
    this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
      duration: 5000
    });
  }
}
