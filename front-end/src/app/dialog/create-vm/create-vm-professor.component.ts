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
  file: any;
  private courseParam: string;
  private corso: CourseModel;
  filename = 'Choose file';

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
    vm.dockerFile = '/var/docker/vm/' + this.file.name;

    // Creazione VM Type
    this.professorService.createVMType(this.corso.name, vm).subscribe(
      (res) => {
        this.dialogRef.close();
        // res è l'ID del VM Type se creata con successo
        this.snackBar.open('VM created successfully', 'OK', {
          duration: 5000
        });
      //  dire al padre che è stata creata una vmType con successo e gestire lo show o meno del button add
      },
      (error) => {
        this.dialogRef.close();
        this.snackBar.open('Error creating VM, try again.', 'OK', {
          duration: 5000
        });
      }
    );
  }

  handleFileSelect($event: any) {
    this.file = $event.target.files[0];
    if (this.file !== undefined) {
      this.filename = this.file.name;
    } else {
      this.filename = 'Choose file';
    }
  }

}
