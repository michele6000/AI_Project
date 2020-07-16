import { Component, OnInit } from '@angular/core';
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-edit-vm-student',
  templateUrl: './edit-vm-student.component.html',
  styleUrls: ['./edit-vm-student.component.css']
})
export class EditVmStudentComponent implements OnInit {
  error: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

  create(f: NgForm) {

  }
}
