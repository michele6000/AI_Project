import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NgForm} from '@angular/forms';
import {StudentService} from '../../services/student.service';
import {GroupModel} from '../../models/group.model';
import {VmModel} from '../../models/vm.model';

@Component({
  selector: 'app-modify-owner',
  templateUrl: './modify-owner.component.html',
  styleUrls: ['./modify-owner.component.css']
})
export class ModifyOwnerComponent implements OnInit {
  error = false;
  vm: VmModel;

  constructor(@Inject(MAT_DIALOG_DATA) public data: GroupModel, private dialogRef: MatDialogRef<ModifyOwnerComponent>, private studentService: StudentService) {
    // this.vm = data;
  }

  ngOnInit(): void {
  }

  modify(f: NgForm) {
    const studentId = f.value.matricola;
    // this.studentService.modifyOwner();
  }

  closeDialog() {
    this.dialogRef.close(false);
  }
}
