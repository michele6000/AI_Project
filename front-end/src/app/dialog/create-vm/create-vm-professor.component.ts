import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {VmType} from '../../models/vm-type.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';

@Component({
  selector: 'app-create-vm',
  templateUrl: './create-vm-professor.component.html',
  styleUrls: ['./create-vm-professor.component.css']
})
export class CreateVmProfessorComponent implements OnInit {
  error = false;
  file: any;
  private courseName: string;
  filename = 'Choose file';

  constructor(@Inject(MAT_DIALOG_DATA) public data: string, private professorService: ProfessorService,
              private snackBar: MatSnackBar, private router: Router, private dialogRef: MatDialogRef<CreateVmProfessorComponent>) {
    this.courseName = data;
  }

  ngOnInit(): void {}

  create(f: NgForm) {
    const vm = new VmType();
    vm.dockerFile = '/var/docker/vm/' + this.file.name;

    // Creazione VM Type
    this.professorService.createVMType(this.courseName, vm).subscribe(
      (res) => {
        this.dialogRef.close(true);
        // res è l'ID del VM Type se creata con successo
        this.snackBar.open('VM Type created successfully', 'OK', {
          duration: 5000
        });
      },
      (error) => {
        this.dialogRef.close(false);
        this.snackBar.open('Error creating VM Type, try again.', 'OK', {
          duration: 5000
        });
      }
    );
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
