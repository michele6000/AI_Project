import {Component, OnInit} from '@angular/core';
import {StudentService} from '../../../services/student.service';
import {CourseModel} from '../../../models/course.model';
import {Router} from '@angular/router';
import {GroupModel} from '../../../models/group.model';

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

  constructor(private studentService: StudentService, private router: Router) {
  }

  ngOnInit(): void {

    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    // Recupero i parametri del corso
    this.course = this.studentService.findCourseByNameUrl(this.courseParam);

    this.studentService.teams.subscribe((teams) => {
      this.team = teams.filter(t => t.status === 1 && t.courseName === this.course.name)[0];

      // Recupero l'elenco di studenti dato il team ID
      this.studentService.findMembersByTeamId(this.team.id).subscribe((students) => {
        this.data = students.filter((s) => s.id !== localStorage.getItem('id'));
      });
    });
  }

}
