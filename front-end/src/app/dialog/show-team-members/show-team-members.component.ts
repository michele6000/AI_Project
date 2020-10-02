import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {GroupModel} from '../../models/group.model';
import {ProfessorService} from '../../services/professor.service';
import {StudentModel} from '../../models/student.model';

@Component({
  selector: 'app-show-team-members',
  templateUrl: './show-team-members.component.html',
  styleUrls: ['./show-team-members.component.css']
})
export class ShowTeamMembersComponent implements OnInit {

  teamName: string;
  studentsInTeam: StudentModel[] = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data, private dialogRef: MatDialogRef<ShowTeamMembersComponent>) {
    this.teamName = data.teamName;
    this.studentsInTeam = data.students;
  }

  ngOnInit(): void {

  }

}
