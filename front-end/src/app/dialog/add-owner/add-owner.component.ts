import {Component, Inject, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {StudentService} from '../../services/student.service';
import {StudentModel} from '../../models/student.model';

@Component({
  selector: 'app-add-owner',
  templateUrl: './add-owner.component.html',
  styleUrls: ['./add-owner.component.css']
})
export class AddOwnerComponent implements OnInit {
  error: any;
  students: StudentModel[];

  constructor(@Inject(MAT_DIALOG_DATA) public data: GroupModel, private dialogRef: MatDialogRef<AddOwnerComponent>, private studentService: StudentService) { }

  ngOnInit(): void {
  }

  addOwner(f: NgForm) {
    const studentId = f.value.matricola;
    //this.studentService.addVmOwner();
  }

  closeDialog() {
    this.dialogRef.close(false);
  }
}
