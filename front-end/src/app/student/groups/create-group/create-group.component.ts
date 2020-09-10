import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../../services/professor.service';
import {StudentModel} from '../../../models/student.model';
import {ActivatedRoute, Router} from '@angular/router';
import {CourseModel} from '../../../models/course.model';
import {StudentService} from "../../../services/student.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {GroupModel} from "../../../models/group.model";

@Component({
  selector: 'app-create-group',
  templateUrl: './create-group.component.html',
  styleUrls: ['./create-group.component.css']
})
export class CreateGroupComponent implements OnInit {
  studentsColumns = ['email', 'name', 'firstName', 'id'];
  studentsData: StudentModel[] = [];
  groupsColumns = ['name', 'proposer'];
  groupsData: GroupModel[] = [];
  innerGroupColumns = ['id', 'name', 'firstName', 'status'];

  course: CourseModel;

  selectedStudents: StudentModel[] = [];
  private courseParam: string;
  error = false;
  message = '';

  constructor(private route: ActivatedRoute, private router: Router, private studentService: StudentService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    // Recupero i parametri del corso
    this.course = this.studentService.findCourseByNameUrl(this.courseParam);

    // Recupero l'elenco degli studenti ancora disponibili
    this.studentService.findAvailableStudentsByCourseName(this.course.name).subscribe(
      (result: StudentModel[]) => {
        this.studentsData = result.filter((s) => s.id !== localStorage.getItem('id'));
      },
      (error: any) => {
        // @todo
        console.log(error);
      }
    );

    // Recupero l'elenco delle proposte inviate / ricevute
    this.studentService.teams.subscribe((teams) => {
      const groupData = teams ? teams.filter(t => t.courseName === this.course.name) : [];

      // Per ogni gruppo del corso recupero l'elenco degli studenti e lo stato
      groupData.forEach((t) => {
        this.studentService.findMembersByTeamId(t.id).subscribe(
          (payload) => {
            t.members = payload;
            this.groupsData = [...this.groupsData, t];
          },
          (error) => {
          }
        );
      });
    });
  }

  proposeGroup(f: NgForm) {
    if (f.valid) {
      if ((this.selectedStudents.length + 1) < this.course.min || (this.selectedStudents.length + 1) > this.course.max) {
        this.error = true;
        this.message = 'Vincoli creazione gruppo non rispettati. Minimo ' + this.course.min
          + ' membri e massimo ' + this.course.max + ' membri (compreso te stesso).';
      } else {
        this.error = false;
        this.message = '';
        this.studentService.proposeTeam(this.selectedStudents, this.course.name, f.value.name).subscribe(
          (response) => {
            // Tutte a buon fine
            this.snackBar.open('Team proposal created successfully.', 'OK', {
              duration: 5000
            });
          },
          (error) => {
            this.snackBar.open('Error creating team proposal, try again.', 'OK', {
              duration: 5000
            });
          }
        );
      }
    }
  }
}
