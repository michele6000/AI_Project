import {Component, OnInit} from '@angular/core';
import {GroupModel} from '../../models/group.model';
import {MatDialog} from "@angular/material/dialog";
import {EditVmProfessorComponent} from '../../dialog/edit-vm/edit-vm-professor.component';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-vms',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements OnInit {
  data = [];
  columns = [];

  constructor(private dialog: MatDialog, private route: ActivatedRoute, private router: Router) {

  }

  ngOnInit(): void {
    console.log(this.router.routerState.snapshot.url.split('/')[2]);
  }

  deleteVM($event: any[]) {

  }

  editGroup(group: GroupModel) {
    this.dialog.open(EditVmProfessorComponent, {data: group})
      .afterClosed()
      .subscribe(result => {
        console.log(result);
      });
  }
}
