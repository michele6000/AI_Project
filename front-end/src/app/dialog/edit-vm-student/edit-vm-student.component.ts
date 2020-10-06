import {Component, Inject, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {VmStudent} from '../../models/vm-student.model';
import {StudentService} from '../../services/student.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-vm-student',
  templateUrl: './edit-vm-student.component.html',
  styleUrls: ['./edit-vm-student.component.css']
})
export class EditVmStudentComponent implements OnInit {
  error = false;
  team: GroupModel;
  limitError = [];
  courseName: string;

  constructor(private studentService: StudentService, @Inject(MAT_DIALOG_DATA) public data: GroupModel, private snackBar: MatSnackBar, private dialogRef: MatDialogRef<EditVmStudentComponent>) {
    this.team = data;
    // this.courseName = ; //TODO: settare sto parametro
  }

  ngOnInit(): void {
  }

  create(f: NgForm) {
    const vm = new VmStudent();
    vm.ram = f.value.ram;
    vm.cpu = f.value.vcpu;
    vm.hdd = f.value.disk;
    this.error = false;
    this.limitError = [];
    if (vm.ram > this.team.limit_ram) {
      this.error = true;
      this.limitError.push('RAM (maximum ' + this.team.limit_ram + ')');
    }
    if (vm.hdd > this.team.limit_hdd) {
      this.error = true;
      this.limitError.push('HDD (maximum ' + this.team.limit_hdd + ')');
    }
    if (vm.cpu > this.team.limit_cpu) {
      this.error = true;
      this.limitError.push('CPU (maximum ' + this.team.limit_cpu + ')');
    }
    if (!this.error) {
      this.studentService.createVm(this.team.id, vm).subscribe(
        (res) => {
          this.dialogRef.close(true);
          this.snackBar.open('VM created successfully', 'OK', {
            duration: 5000
          });
        },
        (error) => {
          this.dialogRef.close(false);
          this.snackBar.open('Error creating VM: ' + error.error.message, 'OK', {
            duration: 5000
          });
        });
    }
  }
}
