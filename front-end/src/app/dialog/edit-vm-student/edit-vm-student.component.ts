import {Component, Inject, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {VmStudent} from '../../models/vm-student.model';
import {StudentService} from '../../services/student.service';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';

@Component({
  selector: 'app-edit-vm-student',
  templateUrl: './edit-vm-student.component.html',
  styleUrls: ['./edit-vm-student.component.css']
})
export class EditVmStudentComponent implements OnInit {
  error = false;
  team: GroupModel;
  limitError = [];

  constructor(private studentService: StudentService, @Inject(MAT_DIALOG_DATA) public data: GroupModel) {
    this.team = data;
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
      this.studentService.createVm(this.team.id, vm).subscribe((res) => {
        /*this.studentService.addVMOwner(res.id, localStorage.getItem('id')).subscribe((resOwner) => {
          console.log(resOwner);
        });*/
      });
    }
  }
}
