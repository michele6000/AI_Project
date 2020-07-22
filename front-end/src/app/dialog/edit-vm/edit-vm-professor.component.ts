import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {NgForm} from '@angular/forms';
import {CrudService} from '../../services/crud.service';
import {VmProfessor} from '../../models/vm-professor.model';

@Component({
  selector: 'app-edit-vm',
  templateUrl: './edit-vm-professor.component.html',
  styleUrls: ['./edit-vm-professor.component.css']
})
export class EditVmProfessorComponent implements OnInit {
  error = false;
  group: GroupModel;
  fileAbsent: boolean = false;
  file: any;

  constructor(@Inject(MAT_DIALOG_DATA) public data: GroupModel, private crudService: CrudService) {
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
    this.crudService.createVMStudent(vm);
  }

  handleFileSelect($event: any) {
    console.log($event);
    this.file = $event.target.files[0];
    this.fileAbsent = false;
  }
}
