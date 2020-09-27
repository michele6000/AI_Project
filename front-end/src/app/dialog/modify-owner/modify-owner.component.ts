import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NgForm} from '@angular/forms';
import {StudentService} from '../../services/student.service';
import {GroupModel} from '../../models/group.model';
import {VmModel} from '../../models/vm.model';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-modify-owner',
  templateUrl: './modify-owner.component.html',
  styleUrls: ['./modify-owner.component.css']
})
export class ModifyOwnerComponent implements OnInit {
  error = false;
  dataVMAndStudents: any;

  constructor(@Inject(MAT_DIALOG_DATA) public data: GroupModel, private dialogRef: MatDialogRef<ModifyOwnerComponent>, private studentService: StudentService,  private snackBar: MatSnackBar) {
    this.dataVMAndStudents = data;
  }

  ngOnInit(): void {
  }

  modifyOwner(f: NgForm) {
    const studentId = f.value.matricola;
    this.studentService.modifyOwner(this.dataVMAndStudents.vm.id, studentId).subscribe(
      res => {
        this.dialogRef.close(res);
        this.snackBar.open('Owner modified successfully.', 'OK', {
          duration: 5000
        });
      },
      error => {
        this.dialogRef.close(error);
        this.snackBar.open('Error modifying owner.', 'OK', {
          duration: 5000
        });
      }
    );
  }

  closeDialog() {
    this.dialogRef.close(false);
  }
}
