import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {CrudService} from '../../services/crud.service';

@Component({
  selector: 'app-edit-homework',
  templateUrl: './edit-homework.component.html',
  styleUrls: ['./edit-homework.component.css']
})
export class EditHomeworkComponent implements OnInit {

  error: boolean = false;
  fileAbsent: boolean = false;
  file: any;
  assignments: any[];

  constructor(@Inject(MAT_DIALOG_DATA) public data: any[], private crudService: CrudService) {
    this.assignments = data;
  }

  ngOnInit(): void {

  }

  handleFileSelect($event: any) {
    console.log($event);
    this.file = $event.target.files[0];
    this.fileAbsent = false;
  }

  send(){

  }

}
