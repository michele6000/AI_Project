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
  usage: GroupModel;
  canCreateVM = false;

  constructor(private dialog: MatDialog, private router: Router, private studentService: StudentService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
    this.corso = this.studentService.findCourseByNameUrl(this.courseParam);
    // Recupero il team al quale appartiene lo studente
    this.studentService.teams.subscribe((teams) => {
        // prendo i team attivi e che fanno parte di quel corso
        const filteredActiveTeams = teams.filter(t => t.status === 1 && t.courseName === this.corso.name);
        if (filteredActiveTeams.length > 0) {
          // attivo il pulsante createVM nella vista
          this.canCreateVM = true;
          this.team = filteredActiveTeams[0];
          // Recupero le VM del team
          this.studentService.findVmsByTeam(this.team.id).subscribe(
            (vms) => {
              this.computeOwner(vms);
            },
            error => {
              this.genericError();
            });
          this.studentService.getTeamStat(this.team.id).subscribe(
            (teamUsage) => {
              this.usage = teamUsage;
            },
            error => {
              this.genericError();
            }
          );
        }
      },
      error => {
        this.genericError();
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
            },
            error => {
              this.genericError();
            });
        }
      });
  }

  deleteVm(vm: VmModel) {
    this.dialog.open(DialogDeleteVmComponent, {data: vm.name})
      .afterClosed()
      .subscribe(result => {
        if (result) {
          this.studentService.deleteVm(vm.id).subscribe(
            (res) => {
              this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => {
                this.computeOwner(vms);
                this.snackBar.open('VM deleted successfully.', 'OK', {
                  duration: 5000
                });
              }, error => {
                this.genericError();
              });
            },
            (error) => {
              this.genericError();
            });
        }
      });
  }

  editVM(vm: VmModel) {
    this.dialog.open(ModifyVmStudentComponent, {data: {vmConfig: vm, team: this.team}})
      .afterClosed()
      .subscribe(result => {
        if (result){
          this.studentService.findTeamsByStudent(localStorage.getItem('id'), true).subscribe(
            resTeams => {
              this.studentService.getVmConfiguration(vm.id).subscribe(
                res => {
                  if (res) {
                    this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => {
                        this.computeOwner(vms);
                      },
                      error => {
                        this.genericError();
                      });
                  }
                }
              );
            }
          );
        }
      });
  }

  modifyOwnerVM(vm: VmModel) {
    // recupero gli studenti del team
    this.studentService.findMembersByTeamId(vm.groupId).subscribe((studentInTeam) => {
        this.studentService.getVmOwners(vm.id).subscribe((owners) => {
            let studentsAvailable;
            const studentsNotAvailableIds = owners.map(s => s.id);
            // filtro solo gli studenti che non sono ancora owners della vm
            studentsAvailable = studentInTeam.filter(s => !studentsNotAvailableIds.includes(s.id));
            this.dialog.open(ModifyOwnerComponent, {data: {vm, students: studentsAvailable}})
              .afterClosed()
              .subscribe(result => {
                if (result) {
                  // recupero le vm
                  this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => {
                      // calcolo gli owner
                      this.computeOwner(vms);
                    },
                    error => {
                      this.genericError();
                    });
                }
              });
          },
          error => {
            this.genericError();
          });
      },
      error => {
        this.genericError();
      });
  }

  addOwnerVM(vm: VmModel) {
    // recupero gli studenti del team
    this.studentService.findMembersByTeamId(vm.groupId).subscribe((studentInTeam) => {
        this.studentService.getVmOwners(vm.id).subscribe((owners) => {
            let studentsAvailable;
            const studentsNotAvailableIds = owners.map(s => s.id);
            // filtro solo gli studenti che non sono ancora owners della vm
            studentsAvailable = studentInTeam.filter(s => !studentsNotAvailableIds.includes(s.id));
            if (studentsAvailable === 0) {
              this.dialog.open(AddOwnerComponent, {data: {vm, students: studentsAvailable}})
                .afterClosed()
                .subscribe(result => {
                  if (result) {
                    this.studentService.findVmsByTeam(this.team.id).subscribe((vms) => {
                        // calcolo gli owner
                        this.computeOwner(vms);
                      },
                      error => {
                        this.genericError();
                      });
                  }
                });
            } else {
              this.snackBar.open('You can\'t add other owners.', 'OK', {
                duration: 5000
              });
            }
          },
          error => {
            this.genericError();
          });
      },
      error => {
        this.genericError();
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
          studentOwners = studentOwners.slice(0, -2);
          vm.owner = studentOwners;
          vm.groupId = this.team.id;
        },
        error => {
          this.genericError();
        });
    });
    this.data = vms;
  }

  genericError() {
    this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
      duration: 5000
    });
    location.reload();
  }
}
