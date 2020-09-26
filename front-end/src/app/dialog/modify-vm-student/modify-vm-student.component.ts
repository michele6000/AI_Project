import {Component, Inject, OnInit} from '@angular/core';
import {GroupModel} from '../../models/group.model';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {VmStudent} from '../../models/vm-student.model';
import {NgForm} from '@angular/forms';
import {StudentService} from '../../services/student.service';
import {VmModel} from '../../models/vm.model';

@Component({
  selector: 'app-modify-vm-student',
  templateUrl: './modify-vm-student.component.html',
  styleUrls: ['./modify-vm-student.component.css']
})
export class ModifyVmStudentComponent implements OnInit {
  error = false;
  vmConfig: VmModel;
  limitError = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data: VmModel, private studentService: StudentService, private snackBar: MatSnackBar, private dialogRef: MatDialogRef<ModifyVmStudentComponent>) {
    this.vmConfig = data;
    console.log("VM config: ");
    console.log(this.vmConfig);
  }

  ngOnInit(): void {}

  edit(f: NgForm){
    const vm = new VmStudent();
    vm.ram = f.value.ram;
    vm.cpu = f.value.vcpu;
    vm.hdd = f.value.disk;
    this.error = false;
    this.limitError = [];
    if (vm.ram > this.vmConfig.ram) {
      this.error = true;
      this.limitError.push('RAM (maximum ' + this.vmConfig.ram + ')');
    }
    if (vm.hdd > this.vmConfig.hdd) {
      this.error = true;
      this.limitError.push('HDD (maximum ' + this.vmConfig.hdd + ')');
    }
    if (vm.cpu > this.vmConfig.cpu) {
      this.error = true;
      this.limitError.push('CPU (maximum ' + this.vmConfig.cpu + ')');
    }
    if (!this.error) {
      this.studentService.modifyConfigurationVm(this.vmConfig.id, vm).subscribe(
        res => {
          this.dialogRef.close();
          this.snackBar.open('VM limits updated successfully', 'OK', {
            duration: 5000
          });
        },
        error => {
          this.dialogRef.close();
          this.snackBar.open('Error updating VM limits', 'OK', {
            duration: 5000
          });
        }
      );
    }
  }

}
