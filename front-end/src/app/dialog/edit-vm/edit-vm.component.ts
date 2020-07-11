import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';

@Component({
  selector: 'app-edit-vm',
  templateUrl: './edit-vm.component.html',
  styleUrls: ['./edit-vm.component.css']
})
export class EditVmComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: GroupModel) {
    console.log(data.name);
  }

  ngOnInit(): void {
  }

}
