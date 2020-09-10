import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {VmType} from '../../models/vm-type.model';

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

  modify(f: NgForm) {
    const vm = new VmType();
    vm.dockerFile = '/var/docker/vm' + Math.random() + '.docker';
    vm.limit_ram = f.value.ram;
    vm.limit_cpu = f.value.vcpu;
    vm.limit_hdd = f.value.disk;
    vm.limit_instance = f.value.maxVm;
    vm.limit_active_instance = f.value.maxActiveVmSimultaneously;
    this.professorService.updateVMType(vm);
  }
}
