import {Component, Inject, OnInit} from '@angular/core';
import {StudentService} from '../../../services/student.service';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {CourseModel} from '../../../models/course.model';
import {VmModel} from '../../../models/vm.model';

@Component({
  selector: 'app-dialog-delete-vm',
  templateUrl: './dialog-delete-vm.component.html',
  styleUrls: ['./dialog-delete-vm.component.css']
})
export class DialogDeleteVmComponent implements OnInit {

  vm: VmModel;

  constructor(@Inject(MAT_DIALOG_DATA) public data: VmModel) {
    this.vm = data;
  }

  ngOnInit(): void {
  }

}
