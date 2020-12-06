import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {StudentService} from "../../services/student.service";

@Component({
  selector: 'app-edit-vm',
  templateUrl: './edit-vm-professor.component.html',
  styleUrls: ['./edit-vm-professor.component.css']
})
export class EditVmProfessorComponent implements OnInit {
  error = false;
  group: GroupModel;
  nameButton = 'Create';
  usage: GroupModel;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private professorService: ProfessorService,
              private studentService: StudentService, private snackBar: MatSnackBar,
              private dialogRef: MatDialogRef<EditVmProfessorComponent>) {
    this.group = data;
    if (this.group !== undefined) {
      this.nameButton = 'Update';
    }
  }

  ngOnInit(): void {
    this.studentService.getTeamStat(this.group.id).subscribe(
      (teamUsage) => {
        this.usage = teamUsage;
      },
      error => {
        this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
          duration: 5000
        });
        this.dialogRef.close(false);
      }
    );
  }

  modify(f: NgForm) {
    // Controllo se la modifica è in conflitto con
    // l'uso attuale di risorse da parte del team

    let valid = true;
    const res = [];
    const resUsage = [];
    let message = 'VM limits for ';
    const messageNext = ' are lower than current team usage.';

    if (f.value.vcpu < this.usage.limit_cpu) {
      valid = false;
      res.push('CPU');
      resUsage.push('CPU: ' + this.usage.limit_cpu);
    }
    if (f.value.disk < this.usage.limit_hdd) {
      valid = false;
      res.push('HDD');
      resUsage.push('HDD: ' + this.usage.limit_hdd);
    }
    if (f.value.ram < this.usage.limit_ram) {
      valid = false;
      res.push('RAM');
      resUsage.push('RAM: ' + this.usage.limit_ram);
    }
    if (f.value.maxVm < this.usage.limit_instance) {
      valid = false;
      res.push('VM instances');
      resUsage.push('VM instances: ' + this.usage.limit_instance);
    }
    if (f.value.maxActiveVmSimultaneously < this.usage.limit_active_instance) {
      valid = false;
      res.push('VM active instances');
      resUsage.push('VM active instances: ' + this.usage.limit_active_instance);
    }

    if (!valid) {
      message = message + res.join(', ') + messageNext + '.' + 'Team usage is ' + resUsage.join(', ');
      this.snackBar.open(message, 'OK', {
        duration: 10000
      });
    } else {
      const groupForm = new GroupModel();
      groupForm.id = this.group.id;
      groupForm.limit_cpu = f.value.vcpu;
      groupForm.limit_hdd = f.value.disk;
      groupForm.limit_instance = f.value.maxVm;
      groupForm.limit_ram = f.value.ram;
      groupForm.limit_active_instance = f.value.maxActiveVmSimultaneously;
      this.professorService.setTeamLimits(this.group.id, groupForm).subscribe(
        (res) => {
          this.dialogRef.close(true);
          this.snackBar.open('VM limits setted successfully', 'OK', {
            duration: 5000
          });
        },
        (error) => {
          this.dialogRef.close(false);
          this.snackBar.open(error.error.message, 'OK', {
            duration: 5000
          });
        });
    }
  }
}
