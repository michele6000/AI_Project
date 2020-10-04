import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {StudentModel} from '../../../models/student.model';
import {ActivatedRoute, Router} from '@angular/router';
import {CourseModel} from '../../../models/course.model';
import {StudentService} from '../../../services/student.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {GroupModel} from '../../../models/group.model';
import {concatMap, toArray} from 'rxjs/operators';
import {forkJoin, from, Observable} from 'rxjs';
import {MatDatepickerInputEvent} from '@angular/material/datepicker';

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

  constructor(private route: ActivatedRoute, private router: Router, private studentService: StudentService, private snackBar: MatSnackBar) {
    const today = new Date();
    this.minDate = new Date();
    this.minDate.setDate(today.getDate() + 1);
    this.maxDate = new Date();
    this.maxDate.setDate(today.getDate() + 15);
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    // Recupero i parametri del corso
    this.course = this.studentService.findCourseByNameUrl(this.courseParam);

    this.initData();
  }

  initData() {
    // Recupero l'elenco degli studenti ancora disponibili
    this.studentService.findAvailableStudentsByCourseName(this.course.name).subscribe((result: StudentModel[]) => {
        this.studentsData = result.filter((s) => s.id !== localStorage.getItem('id'));
      },
      (error: any) => {
        this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
          duration: 5000
        });
        location.reload();
      }
    );

    // Recupero l'elenco delle proposte inviate / ricevute
    this.studentService.teams.subscribe((teams) => {
      const groupData = teams ? teams.filter(t => t.courseName === this.course.name) : [];

      /*from(groupData).pipe(
        concatMap((t) => this.studentService.findMembersByTeamId(t.id) as Observable<StudentModel[]>),
        toArray()
      ).subscribe((res) => console.log(res));*/

      // Per ogni gruppo del corso recupero l'elenco degli studenti e lo stato
      groupData.forEach((t) => {

        forkJoin(
          // as of RxJS 6.5+ we can use a dictionary of sources
          {
            pendent: this.studentService.findPendentStudentsByTeamId(t.id),
            confirmed: this.studentService.findConfirmedStudentsByTeamId(t.id),
          }
        ).subscribe((res) => {
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
        });

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
        this.studentService.proposeTeam(this.selectedStudents, this.course.name, f.value.name, this.chosenTimeout).subscribe(
          (response) => {
            // Tutte a buon fine
            this.initData();
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

  timeoutChoseValue($event: MatDatepickerInputEvent<any>) {
    this.chosenTimeout = $event.target.value;
  }
}
