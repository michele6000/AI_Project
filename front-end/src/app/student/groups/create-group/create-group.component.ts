import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {StudentModel} from '../../../models/student.model';
import {ActivatedRoute, Router} from '@angular/router';
import {CourseModel} from '../../../models/course.model';
import {StudentService} from '../../../services/student.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {GroupModel} from '../../../models/group.model';
import {forkJoin, from} from 'rxjs';
import {MatDatepickerInputEvent} from '@angular/material/datepicker';
import {ProfessorService} from '../../../services/professor.service';
import {concatMap, toArray} from 'rxjs/operators';

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
  error = false;
  message = '';
  private courseParam: string;
  minDate: Date;
  maxDate: Date;
  chosenTimeout: Date;
  loaderDisplayed = false;
  private changeCorsoSub;

  constructor(private route: ActivatedRoute, private router: Router, private studentService: StudentService,
              private professorService: ProfessorService, private snackBar: MatSnackBar) {
    const today = new Date();
    this.minDate = new Date();
    this.minDate.setDate(today.getDate() + 1);
    this.maxDate = new Date();
    this.maxDate.setDate(today.getDate() + 15);
  }

  ngOnInit(): void {
    this.changeCorsoSub = this.studentService.eventsSubjectChangeCorsoSideNav.subscribe(next => {
        this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
        // Recupero i parametri del corso
        this.course = this.studentService.findCourseByNameUrl(this.courseParam);
        this.initData();
      },
      error => {
        this.genericError();
      });
  }

  initData() {
    // Recupero l'elenco degli studenti ancora disponibili
    this.professorService.getEnrolledStudents(this.course.name).subscribe((result: StudentModel[]) => {
        // filtro per rimuovere lo studente loggato da quelli disponibili
        this.studentsData = result.filter((s) => s.id !== localStorage.getItem('id'));
      },
      (error) => {
        this.genericError();
      }
    );
    // Recupero l'elenco delle proposte inviate / ricevute
    this.studentService.teams.subscribe((teams) => {
      this.groupsData = [];
      const groupData = teams ? teams.filter(t => t.courseName === this.course.name) : [];
      // Per ogni gruppo del corso recupero l'elenco degli studenti e lo stato (confirmed o pendent)
      groupData.forEach((t) => {
        forkJoin(
          {
            pendent: this.studentService.findPendentStudentsByTeamId(t.id),
            confirmed: this.studentService.findConfirmedStudentsByTeamId(t.id),
          }
        ).subscribe((res) => {
            // Merge dei 2 array in uno solo (t.members) settando anche lo stato (confirmed/pendent)
            t.members = [];
            res.confirmed.forEach((c) => {
              c.status = 'Confirmed';
              t.members.push(c);
            });
            res.pendent.forEach(c => {
              c.status = 'Pendent';
              t.members.push(c);
            });
            this.groupsData = [...this.groupsData, t];
          },
          error => {
            this.genericError();
          });
      });
    });
  }

  proposeGroup(f: NgForm) {
    if (f.valid) {
      if ((this.selectedStudents.length + 1) < this.course.min || (this.selectedStudents.length + 1) > this.course.max) {
        this.error = true;
        this.message = 'Team proposal constraints not satisfied. Minimum ' + this.course.min
          + ' students and maximum ' + this.course.max + ' students (including yourself).';
      } else {
        this.error = false;
        this.message = '';
        this.loaderDisplayed = true;
        this.studentService.proposeTeam(this.selectedStudents, this.course.name, f.value.name, this.chosenTimeout).subscribe(
          (response) => {
            // Tutte a buon fine
            this.studentService.findTeamsByStudent(localStorage.getItem('id'));
            this.loaderDisplayed = false;
            this.selectedStudents = [];
            this.snackBar.open('Team proposal created successfully.', 'OK', {
              duration: 5000
            });
          },
          (error) => {
            this.loaderDisplayed = false;
            this.selectedStudents=[];
            this.snackBar.open(error.error.message, 'OK', {
              duration: 5000
            });
          }
        );
      }
    }
  }

  timeoutChoseValue($event: MatDatepickerInputEvent<any>) {
    this.chosenTimeout = $event.target.value;
  }

  genericError() {
    this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
      duration: 5000
    });
  }

  deleteAllProposal() {
    const res = from(this.groupsData).pipe(
      concatMap(t => {
        return this.studentService.deleteAllProposal(t.id);
      }),
      toArray()
    );

    res.subscribe((result: boolean[]) => {
        if (result.filter(e => !e).length > 0) {
          // Almeno una ha fallito
          this.snackBar.open('Error deleting.', 'OK', {
            duration: 5000
          });
        } else {
          // Tutte a buon fine
          this.studentService.findTeamsByStudent(localStorage.getItem('id'));
          this.snackBar.open('Team proposals deleted successfully.', 'OK', {
            duration: 5000
          });
        }
      },
      error => {
        this.genericError();
      });
  }
}
