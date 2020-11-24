import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {VmStudent} from '../../models/vm-student.model';
import {NgForm} from '@angular/forms';
import {StudentService} from '../../services/student.service';
import {VmModel} from '../../models/vm.model';
import {GroupModel} from '../../models/group.model';

@Component({
  selector: 'app-modify-vm-student',
  templateUrl: './modify-vm-student.component.html',
  styleUrls: ['./modify-vm-student.component.css']
})
export class ModifyVmStudentComponent implements OnInit {
  error = false;
  vmConfigAndLimitsPerTeam: any;
  limitError = [];
  courseName: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: VmModel, private studentService: StudentService,
              private dialogRef: MatDialogRef<ModifyVmStudentComponent>, private snackBar: MatSnackBar) {
    this.vmConfigAndLimitsPerTeam = data;
    this.courseName = this.vmConfigAndLimitsPerTeam.team.courseName;
    console.log('In dialog');
    console.log(data);
  }

  ngOnInit(): void {
  }

  edit(f: NgForm) {
    const vm = new VmStudent();
    vm.ram = f.value.ram;
    vm.cpu = f.value.vcpu;
    vm.hdd = f.value.disk;
    this.error = false;
    this.limitError = [];
    if (vm.ram > this.vmConfigAndLimitsPerTeam.team.limit_ram) {
      this.error = true;
      this.limitError.push('RAM (maximum ' + this.vmConfigAndLimitsPerTeam.team.limit_ram + ')');
    }
    if (vm.hdd > this.vmConfigAndLimitsPerTeam.team.limit_hdd) {
      this.error = true;
      this.limitError.push('HDD (maximum ' + this.vmConfigAndLimitsPerTeam.team.limit_hdd + ')');
    }
    if (vm.cpu > this.vmConfigAndLimitsPerTeam.team.limit_cpu) {
      this.error = true;
      this.limitError.push('CPU (maximum ' + this.vmConfigAndLimitsPerTeam.team.limit_cpu + ')');
    }
    if (!this.error) {
      this.studentService.modifyConfigurationVm(this.vmConfigAndLimitsPerTeam.vmConfig.id, vm).subscribe(
        res => {
          this.snackBar.open('Vm configuration modified successfully.', 'OK', {
            duration: 5000
          });
          this.dialogRef.close(true);
        },
        error => {
          this.snackBar.open('Error modifying vm configuration. ' + error.error.message, 'OK', {
            duration: 5000
          });
          this.dialogRef.close(false);
        }
      );
    }
  }
}
