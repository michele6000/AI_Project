import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {StudentModel} from '../../models/student.model';

@Component({
  selector: 'app-show-team-members',
  templateUrl: './show-team-members.component.html',
  styleUrls: ['./show-team-members.component.css']
})
export class ShowTeamMembersComponent implements OnInit {

  teamName: string;
  studentsInTeam: StudentModel[] = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data) {
    this.teamName = data.teamName;
    this.studentsInTeam = data.students;
  }

  ngOnInit(): void {
  }

}
