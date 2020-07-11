import {Component, OnInit} from '@angular/core';
import {GroupModel} from '../../models/group.model';
import {MatDialog} from "@angular/material/dialog";
import {EditVmComponent} from "../../dialog/edit-vm/edit-vm.component";

@Component({
  selector: 'app-vms',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements OnInit {
  data = [];
  columns = [];

  constructor(private dialog: MatDialog) {
  }

  ngOnInit(): void {
  }

  deleteVM($event: any[]) {

  }

  editGroup(group: GroupModel) {
    this.dialog.open(EditVmComponent, {data: group})
      .afterClosed()
      .subscribe(result => {
        console.log(result);
      });
  }
}
