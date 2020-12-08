import {Component, OnInit} from '@angular/core';
import {StudentService} from '../../../services/student.service';
import {CourseModel} from '../../../models/course.model';
import {Router} from '@angular/router';
import {GroupModel} from '../../../models/group.model';
import {ProfessorService} from "../../../services/professor.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-info-group',
  templateUrl: './info-group.component.html',
  styleUrls: ['./info-group.component.css']
})
export class InfoGroupComponent implements OnInit {
  columns = ['email', 'name', 'firstName', 'id'];
  data = [];
  courseParam: string;
  course: CourseModel;
  team: GroupModel;
  private changeCorsoSub;

  constructor(private studentService: StudentService, private router: Router, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {

    this.changeCorsoSub = this.studentService.eventsSubjectChangeCorsoSideNav.subscribe(next => {
        this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
        // Recupero i parametri del corso
        this.course = this.studentService.findCourseByNameUrl(this.courseParam);
        this.studentService.teams.subscribe((teams) => {
          this.team = teams.filter(t => t.status === 1 && t.courseName === this.course.name)[0];
          // Recupero l'elenco di studenti dato il team ID
          this.studentService.findMembersByTeamId(this.team.id).subscribe((students) => {
            this.data = students;
          });
        });
      },
      error => {
        this.genericError();
      });
  }

  genericError() {
    this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
      duration: 5000
    });
  }

}
