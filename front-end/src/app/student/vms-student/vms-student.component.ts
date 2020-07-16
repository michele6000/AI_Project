import { Component, OnInit } from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {EditVmProfessorComponent} from "../../dialog/edit-vm/edit-vm-professor.component";
import {EditVmStudentComponent} from "../../dialog/edit-vm-student/edit-vm-student.component";

@Component({
  selector: 'app-vms-student',
  templateUrl: './vms-student.component.html',
  styleUrls: ['./vms-student.component.css']
})
export class VmsStudentComponent implements OnInit {

  constructor(private dialog: MatDialog,) { }

  ngOnInit(): void {
  }

  createVM() {
    this.dialog.open(EditVmStudentComponent)
      .afterClosed()
      .subscribe(result => {
        console.log(result);
      });
  }
}
