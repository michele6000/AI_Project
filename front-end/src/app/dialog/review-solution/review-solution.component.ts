import {Component, Inject, OnInit} from '@angular/core';
import {SolutionModel} from '../../models/solution.model';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ProfessorService} from '../../services/professor.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NgForm} from '@angular/forms';

@Component({
  selector: 'app-review-solution',
  templateUrl: './review-solution.component.html',
  styleUrls: ['./review-solution.component.css']
})
export class ReviewSolutionComponent implements OnInit {
  private solution: SolutionModel;
  revision: string;
  error = false;
  errorStr = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data: SolutionModel, private professorService: ProfessorService,
              private dialogRef: MatDialogRef<ReviewSolutionComponent>, private snackBar: MatSnackBar) {
    this.solution = data;
  }

  ngOnInit(): void {
  }

  review(f: NgForm) {
    const revision = f.value.revision;

    this.professorService.reviewSolution(this.solution.matricola, this.solution.id, revision).subscribe((res) => {
      this.snackBar.open('Solution reviewed successfully.', 'OK', {
        duration: 5000
      });
      this.dialogRef.close(true);
    }, (error) => {
      this.snackBar.open('Error reviewing solution.', 'OK', {
        duration: 5000
      });
    });
  }
}
