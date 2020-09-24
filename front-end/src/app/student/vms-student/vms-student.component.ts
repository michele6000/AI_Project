import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {EditVmStudentComponent} from '../../dialog/edit-vm-student/edit-vm-student.component';
import {StudentService} from '../../services/student.service';
import {Router} from '@angular/router';
import {CourseModel} from '../../models/course.model';
import {GroupModel} from '../../models/group.model';
import {VmModel} from '../../models/vm.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DialogDeleteVmComponent} from './dialog-delete-vm/dialog-delete-vm.component';
import {ModifyVmStudentComponent} from '../../dialog/modify-vm-student/modify-vm-student.component';

@Component({
  selector: 'app-vms-student',
  templateUrl: './vms-student.component.html',
  styleUrls: ['./vms-student.component.css']
})
export class VmsStudentComponent implements OnInit {
  data = [];
  columns = ['owner'];
  courseParam: string;
  corso: CourseModel;
  team: GroupModel;

  constructor(private dialog: MatDialog, private router: Router, private studentService: StudentService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    this.corso = this.studentService.findCourseByNameUrl(this.courseParam);

    // Recupero il team al quale appartiene lo studente
    this.studentService.teams.subscribe((teams) => {
      if (teams.filter(t => t.status === 1 && t.courseName === this.corso.name).length > 0) {
        this.team = teams.filter(t => t.status === 1 && t.courseName === this.corso.name)[0];

        // Recupero le VM del team
        this.studentService.findVmsByTeam(this.team.id).subscribe(
          (vms) => {
            const vmList = [];
            vms.forEach((vm) => {
              this.studentService.getVmOwners(vm.id).subscribe((owners) => {
                let studentOwners = '';
                // concateno gli id degli studenti (owners della VM)
                owners.forEach(o => {
                    studentOwners += o.id;
                    studentOwners += ' ';
                });
                // elimino dalla stringa l'ultima virgola alla fine
                // studentOwners.substr(0, studentOwners.length - 3);
                vm.owner = studentOwners;
                vm.groupId = this.team.id;
              });
            });
            this.data = vms;
          });
      }
    });

  }

  createVM() {
    this.dialog.open(EditVmStudentComponent, {data: this.team})
      .afterClosed()
      .subscribe(result => {
        console.log(result);
      });
  }

  deleteVm($event: VmModel) {
    this.dialog.open(DialogDeleteVmComponent, {data: $event})
      .afterClosed()
      .subscribe(result => {
        if (result) {
          this.studentService.deleteVm($event.id).subscribe(
            (res) => {
              this.snackBar.open('VM deleted successfully.', 'OK', {
                duration: 5000
              });
              this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => this.data = vms);
            },
            (error) => {
            });
        }
      });

  }

  editVM(vm: VmModel){
    this.dialog.open(ModifyVmStudentComponent, {data: vm})
      .afterClosed()
      .subscribe(result => {
        this.studentService.getVmConfiguration(vm.id).subscribe(
          res => {
            // @TODO -> riassegnare la VM config aggiornata nel gruppo
          }
        );
      });
  }
}
