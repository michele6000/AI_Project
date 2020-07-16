import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {NgForm} from '@angular/forms';

@Component({
  selector: 'app-edit-vm',
  templateUrl: './edit-vm-professor.component.html',
  styleUrls: ['./edit-vm-professor.component.css']
})
export class EditVmProfessorComponent implements OnInit {
  error = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: GroupModel) {
    console.log(data.name);
  }

  ngOnInit(): void {
  }

  create(f: NgForm) {

  }
}
