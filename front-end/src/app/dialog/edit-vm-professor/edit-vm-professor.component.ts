import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {VmType} from '../../models/vm-type.model';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-vm',
  templateUrl: './edit-vm-professor.component.html',
  styleUrls: ['./edit-vm-professor.component.css']
})
export class EditVmProfessorComponent implements OnInit {
  error = false;
  group: GroupModel;
  nameButton = 'Create';

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private professorService: ProfessorService, private snackBar: MatSnackBar, private dialogRef: MatDialogRef<EditVmProfessorComponent>) {
    this.group = data;
    if (this.group !== undefined){
      this.nameButton = 'Update';
    }
  }

  ngOnInit(): void {}

  modify(f: NgForm) {
    const groupForm = new GroupModel();
    groupForm.id = this.group.id;
    groupForm.limit_cpu = f.value.vcpu;
    groupForm.limit_hdd = f.value.disk;
    groupForm.limit_instance = f.value.maxVm;
    groupForm.limit_ram = f.value.ram;
    groupForm.limit_active_instance = f.value.maxActiveVmSimultaneously;
    this.professorService.setTeamLimits(this.group.id, groupForm).subscribe(
      (res) => {
        this.dialogRef.close();
        this.snackBar.open('VM limits setted successfully', 'OK', {
          duration: 5000
        });
      },
      (error) => {
        this.dialogRef.close();
        this.snackBar.open('Error setting limits', 'OK', {
          duration: 5000
        });
      });
  }


}
