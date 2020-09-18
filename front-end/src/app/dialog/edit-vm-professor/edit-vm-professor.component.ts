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
    const groupForm = new GroupModel();
    groupForm.id = this.group.id;
    groupForm.limit_cpu = f.value.vcpu;
    groupForm.limit_hdd = f.value.disk;
    groupForm.limit_instance = f.value.maxVm;
    groupForm.limit_ram = f.value.ram;
    groupForm.limit_active_instance = f.value.maxActiveVmSimultaneously;
    this.professorService.setTeamLimits(this.group.id, groupForm).subscribe(res => console.log(res));
  }
}
