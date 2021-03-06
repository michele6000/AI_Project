import {Component, Inject, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {StudentService} from '../../services/student.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-owner',
  templateUrl: './add-owner.component.html',
  styleUrls: ['./add-owner.component.css']
})
export class AddOwnerComponent implements OnInit {
  error: any;
  dataVMAndStudents: any;

  constructor(@Inject(MAT_DIALOG_DATA) public data: GroupModel, private dialogRef: MatDialogRef<AddOwnerComponent>,
              private studentService: StudentService, private snackBar: MatSnackBar) {
    this.dataVMAndStudents = data;
  }

  ngOnInit(): void {
  }

  addOwner(f: NgForm) {
    const studentId = f.value.matricola;
    this.studentService.addVmOwner(this.dataVMAndStudents.vm.id, studentId).subscribe(
      res => {
        this.dialogRef.close(true);
        this.snackBar.open('Owner added successfully.', 'OK', {
          duration: 5000
        });
      },
      error => {
        this.dialogRef.close(false);
        this.snackBar.open('Error adding owner.', 'OK', {
          duration: 5000
        });
      }
    );
  }
}
