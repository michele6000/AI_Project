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

  constructor(private studentService: StudentService, @Inject(MAT_DIALOG_DATA) public data: GroupModel) {
    this.team = data;
  }

  ngOnInit(): void {
  }

  create(f: NgForm) {
    const vm = new VmStudent();
    vm.ram = f.value.ram;
    vm.vcpu = f.value.vcpu;
    vm.disk = f.value.disk;
    this.studentService.createVM(this.team.id, vm).subscribe((res) => {
      console.log(res);
    });
  }
}
