import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-delete-vm',
  templateUrl: './dialog-delete-vm.component.html',
  styleUrls: ['./dialog-delete-vm.component.css']
})
export class DialogDeleteVmComponent implements OnInit {
  name: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: string) {
    this.name = data;
  }

  ngOnInit(): void {
  }

}
