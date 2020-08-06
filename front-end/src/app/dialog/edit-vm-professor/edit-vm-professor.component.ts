import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {VmProfessor} from '../../models/vm-professor.model';

@Component({
  selector: 'app-edit-vm',
  templateUrl: './edit-vm-professor.component.html',
  styleUrls: ['./edit-vm-professor.component.css']
})
export class EditVmProfessorComponent implements OnInit {
  error = false;
  group: GroupModel;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private professorService: ProfessorService) {
    console.log(data);
    this.group = data;
  }

  ngOnInit(): void {
  }

  create(f: NgForm) {
    const vm = new VmProfessor();
    vm.ram = f.value.ram;
    vm.vcpu = f.value.vcpu;
    vm.disk = f.value.disk;
    vm.maxVm = f.value.maxVm;
    vm.maxActiveVmSimultaneously = f.value.maxActiveVmSimultaneously;
    this.professorService.createVMTeacher(vm);
  }
}
