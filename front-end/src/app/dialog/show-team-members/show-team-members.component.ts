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

  team: GroupModel;
  studentsInTeam: StudentModel[] = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data, private dialogRef: MatDialogRef<ShowTeamMembersComponent>, private professorService: ProfessorService) {
    this.team = data;
  }

  ngOnInit(): void {
    this.professorService.findMembersByTeamId(this.data.id).subscribe( res => {
      this.studentsInTeam = res;
    });
  }

  closeDialog() {
    this.dialogRef.close(false);
  }

}
