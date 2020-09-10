import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {VmType} from '../../models/vm-type.model';
import {MatSnackBar} from "@angular/material/snack-bar";
import {CourseModel} from "../../models/course.model";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-vm',
  templateUrl: './create-vm-professor.component.html',
  styleUrls: ['./create-vm-professor.component.css']
})
export class CreateVmProfessorComponent implements OnInit {
  error = false;
  group: GroupModel;
  fileAbsent = false;
  file: any;
  private courseParam: string;
  private corso: CourseModel;

  // @todo Se non ho ancora un VM Type associato al corso visualizzo il bottone di creazione
  // altrimenti quello di modifica

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private professorService: ProfessorService,
              private snackBar: MatSnackBar, private router: Router, private dialogRef: MatDialogRef<CreateVmProfessorComponent>) {
    this.group = data;
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    this.corso = this.professorService.findCourseByNameUrl(this.courseParam);
  }

  create(f: NgForm) {
    const vm = new VmType();
    vm.dockerFile = '/var/docker/vm' + (Math.random() * 10000) + '.docker';
    vm.limit_ram = f.value.ram;
    vm.limit_cpu = f.value.vcpu;
    vm.limit_hdd = f.value.disk;
    vm.limit_instance = f.value.maxVm;
    vm.limit_active_instance = f.value.maxActiveVmSimultaneously;

    // Creazione VM Type
    this.professorService.createVMType(vm).subscribe(
      (res) => {
        // res è l'ID del VM Type se creata con successo
        // Associo il VM Type al corso
        this.professorService.setVMType(this.corso.name, res).subscribe(
          (res2) => {
            this.snackBar.open('VM created successfully', 'OK', {
              duration: 5000
            });
          },
          (error2) => {
            this.snackBar.open('Error associating VM to course, try again.', 'OK', {
              duration: 5000
            });
          }
        );
      },
      (error) => {
        this.snackBar.open('Error creating VM, try again.', 'OK', {
          duration: 5000
        });
      }
    );
  }

  handleFileSelect($event: any) {
    console.log($event);
    this.file = $event.target.files[0];
    this.fileAbsent = false;
  }

  closeDialog() {
    this.dialogRef.close(false);
  }
}
