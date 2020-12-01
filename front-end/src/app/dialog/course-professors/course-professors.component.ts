import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {ProfessorModel} from '../../models/professor.model';

@Component({
  selector: 'app-course-professors',
  templateUrl: './course-professors.component.html',
  styleUrls: ['./course-professors.component.css']
})
export class CourseProfessorsComponent implements OnInit {

  professors: ProfessorModel[] = [];
  courseName = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data) {
    this.professors = data.professors;
    this.courseName = data.courseName;
  }

  ngOnInit(): void {
  }

}
