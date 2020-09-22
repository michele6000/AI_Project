import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {ProfessorService} from '../../services/professor.service';
import {NgForm} from '@angular/forms';

@Component({
  selector: 'app-edit-homework',
  templateUrl: './edit-homework.component.html',
  styleUrls: ['./edit-homework.component.css']
})
export class EditHomeworkComponent implements OnInit {

  error = false;
  fileAbsent = false;
  file: any;
  assignments: any[];
  showInputGrade = false;
  history: any = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data: any[], private professorService: ProfessorService) {
    this.assignments = data;
  }

  ngOnInit(): void {

  }

  handleFileSelect($event: any) {
    console.log($event);
    this.file = $event.target.files[0];
    this.fileAbsent = false;
  }

  send(f: NgForm) {

  }

  showGrade($event) {
    if ($event.value === 0) {
      this.showInputGrade = false;
    } else {
      this.showInputGrade = true;
    }
  }


  handleShowSolution(id: any) {

  }
}
