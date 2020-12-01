import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {SolutionModel} from '../../models/solution.model';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-evaluate-solution',
  templateUrl: './evaluate-solution.component.html',
  styleUrls: ['./evaluate-solution.component.css']
})
export class EvaluateSolutionComponent implements OnInit {
  private solution: SolutionModel;
  evaluation: number;
  error = false;
  errorStr = '';
  filename: string = 'Choose File';
  file: File;
  message: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: SolutionModel, private professorService: ProfessorService,
              private dialogRef: MatDialogRef<EvaluateSolutionComponent>, private snackBar: MatSnackBar) {
    this.solution = data;
  }

  ngOnInit(): void {
  }

  evaluate(f: NgForm) {
    const evaluation = f.value.evaluation;
    const message = f.value.message;
    if (evaluation < 1 || evaluation > 31) {
      this.error = true;
      this.errorStr = 'Error. Range of evaluation: 1 - 31';
    } else {
      this.error = false;

      this.professorService.evaluateSolution(this.solution.matricola, this.solution.id, evaluation, message, this.file).subscribe((res) => {
        this.snackBar.open('Solution evaluated successfully.', 'OK', {
          duration: 5000
        });
        this.dialogRef.close(true);
      }, (error) => {
        this.snackBar.open('Error evaluating solution.', 'OK', {
          duration: 5000
        });
      });
    }
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
