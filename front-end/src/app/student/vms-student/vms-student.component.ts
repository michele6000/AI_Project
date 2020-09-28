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
import {ModifyOwnerComponent} from '../../dialog/modify-owner/modify-owner.component';
import {AddOwnerComponent} from '../../dialog/add-owner/add-owner.component';

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
  canCreateVM = false;

  constructor(private dialog: MatDialog, private router: Router, private studentService: StudentService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
    this.corso = this.studentService.findCourseByNameUrl(this.courseParam);
    // Recupero il team al quale appartiene lo studente
    this.studentService.teams.subscribe((teams) => {
      if (teams.filter(t => t.status === 1 && t.courseName === this.corso.name).length > 0) {
        this.canCreateVM = true;
        this.team = teams.filter(t => t.status === 1 && t.courseName === this.corso.name)[0];
        // Recupero le VM del team
        this.studentService.findVmsByTeam(this.team.id).subscribe(
          (vms) => {
            this.computeOwner(vms);
          });
      }
    });
  }

  createVM() {
    this.dialog.open(EditVmStudentComponent, {data: this.team})
      .afterClosed()
      .subscribe(result => {
        if (result) {
          this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => {
            // calcolo gli owner
            this.computeOwner(vms);
          });
        }
      });
  }

  deleteVm($event: VmModel) {
    this.dialog.open(DialogDeleteVmComponent, {data: $event})
      .afterClosed()
      .subscribe(result => {
        if (result) {
          this.studentService.deleteVm($event.id).subscribe(
            (res) => {
              this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => {
                this.computeOwner(vms);
                this.snackBar.open('VM deleted successfully.', 'OK', {
                  duration: 5000
                });
              });
            },
            (error) => {
              this.snackBar.open('Error deleting VM.', 'OK', {
                duration: 5000
              });
            });
        }
      });

  }

  editVM(vm: VmModel) {
    this.dialog.open(ModifyVmStudentComponent, {data: {vmConfig: vm, team: this.team}})
      .afterClosed()
      .subscribe(result => {
        this.studentService.getVmConfiguration(vm.id).subscribe(
          res => {
            this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => {
              this.computeOwner(vms);
            });
          }
        );
      });
  }

  modifyOwnerVM(element: VmModel) {
    // recupero gli studenti del team
    this.studentService.findMembersByTeamId(element.groupId).subscribe((studentInTeam) => {
      // filtro gli studenti eliinando dalla lista lo studente loggato
      // @Todo: togliere anche gli studenti che sonodiventati owner successivamente
      studentInTeam = studentInTeam.filter((s) => s.id !== localStorage.getItem('id'));
      this.dialog.open(ModifyOwnerComponent, {data: {vm: element, students: studentInTeam}})
        .afterClosed()
        .subscribe(result => {
          if (result) {
            // recupero le vm
            this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => {
              // calcolo gli owner
              this.computeOwner(vms);
            });
          }
        });
    });
  }

  addOwnerVM(element: VmModel) {
    // recupero gli studenti del team
    this.studentService.findMembersByTeamId(element.groupId).subscribe((studentInTeam) => {
      // filtro gli studenti eliinando dalla lista lo studente loggato
      // @Todo: togliere anche gli studenti che sonodiventati owner successivamente
      studentInTeam = studentInTeam.filter((s) => s.id !== localStorage.getItem('id'));
      this.dialog.open(AddOwnerComponent, {data: {vm: element, students: studentInTeam}})
        .afterClosed()
        .subscribe(result => {
          if (result) {
            this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => {
              // calcolo gli owner
              this.computeOwner(vms);
            });
          }
        });
    });
  }

  computeOwner(vms: VmModel[]) {
    const vmList = [];
    vms.forEach((vm) => {
      this.studentService.getVmOwners(vm.id).subscribe((owners) => {
        let studentOwners = '';
        // concateno gli studenti (owners della VM)
        owners.forEach(o => {
          studentOwners += o.name + ' ' + o.firstName;
          studentOwners += ', ';
        });
        // elimino dalla stringa l'ultima virgola alla fine
        studentOwners.slice(0, -1);
        vm.owner = studentOwners;
        vm.groupId = this.team.id;
      });
    });
    this.data = vms;
  }
}
