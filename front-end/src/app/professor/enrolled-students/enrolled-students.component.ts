import {Component, OnDestroy, OnInit} from '@angular/core';
import {concatMap, toArray} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {from, Subscription} from 'rxjs';
import {CourseModel} from '../../models/course.model';
import {StudentModel} from '../../models/student.model';
import {HttpClient} from '@angular/common/http';
import {ProfessorService} from '../../services/professor.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {GroupModel} from '../../models/group.model';
import {MatDialog} from '@angular/material/dialog';
import {ShowTeamMembersComponent} from '../../dialog/show-team-members/show-team-members.component';

@Component({
  selector: 'app-enrolled-students',
  templateUrl: './enrolled-students.component.html',
  styleUrls: ['./enrolled-students.component.css']
})
export class EnrolledStudentsComponent implements OnInit, OnDestroy {
  teams: GroupModel[] = [];
  corso: CourseModel;
  columns = ['email', 'firstName', 'name', 'id'];
  columnsTeam = ['id','name', 'status'];
  data: StudentModel[] = [];
  fileAbsent = true;
  file: any;
  courseParam: string;
  students: StudentModel[] = [];
  existTeam: boolean = false;
  private changeCorsoSub: Subscription;

  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private professorService: ProfessorService, private snackBar: MatSnackBar, private dialog: MatDialog) {
    // sono in ascolto sull'observable (change del corso nella sidenav)
    this.changeCorsoSub = this.professorService.eventsSubjectChangeCorsoSideNav.subscribe(next => {
      this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
      this.corso = this.professorService.findCourseByNameUrl(this.courseParam);

      if (this.corso.name.length > 0) {
        // recupero gli studenti iscritti al corso
        this.professorService.getEnrolledStudents(this.corso.name).subscribe(
          (res) => {
            this.data = res;
          }
        );
      }
      // recupero la lista di studenti da cui pescare gli studenti per iscriverli al corso
      this.professorService.getStudents().subscribe(
        (students) => {
          if (students) {
            this.students = students;
          } else {
            this.students = [];
          }
        }
      );

      this.professorService.findTeamsByCourse(this.corso.name).subscribe(teams => {
        if (teams.length > 0) {
          this.existTeam = true;
          this.teams = teams;
        } else {
          this.existTeam = false;
          this.teams = [];
        }
      });
    });
  }

  ngOnInit(): void {

  }

  deleteStudent($event: StudentModel[]) {
    const res = from($event).pipe(
      concatMap(s => {
        return this.professorService.deleteStudent(this.corso.name, s.id);
      }),
      toArray()
    );

    res.subscribe((result: boolean[]) => {
      this.professorService.getEnrolledStudents(this.corso.name).subscribe((students) => this.data = students);
      if (result.filter(e => !e).length > 0) {
        // Almeno una ha fallito
        this.snackBar.open('Error deleting successfully.', 'OK', {
          duration: 5000
        });
      } else {
        // Tutte a buon fine
        this.snackBar.open('Students deleted successfully.', 'OK', {
          duration: 5000
        });
      }
    });
  }

  addStudent($event: StudentModel) {
    this.professorService.enrollStudent(this.corso.name, $event.id).subscribe((res) => {
      this.professorService.getEnrolledStudents(this.corso.name).subscribe((students) => this.data = students);
      this.snackBar.open('Student added successfully.', 'OK', {
        duration: 5000
      });
    }, (error) => {
      this.snackBar.open('Error adding student. ' + error.statusText + ' ' + error.error.message, 'OK', {
        duration: 5000
      });
    });
  }

  handleFileSelect($event: any) {
    this.file = $event.target.files[0];
    this.fileAbsent = false;
  }

  sendFile() {
    const formData: FormData = new FormData();
    formData.append('file', new Blob([this.file], {type: 'text/csv'}), this.file.name);
    const headers = {
      'Content-Type': 'multipart/form-data'
    };
    this.http.post('/api/API/courses/' + this.corso.name + '/enrollMany', formData)
      .subscribe(
        (result: boolean[]) => {
          if (result.filter((r) => !r).length > 0) {
            // Almeno un caricamento fallito
            this.snackBar.open('Error while uploading ' + result.filter((r) => !r).length + ' student(s). Try again.', 'OK', {
              duration: 5000
            });
          } else {
            this.snackBar.open(result.length + ' students added successfully.', 'OK', {
              duration: 5000
            });
          }
        },
        error => {
          this.snackBar.open('Error while uploading student list. Incorrect format.', 'OK', {
            duration: 5000
          });
        }
      );
  }

  showStudentsInTeam(team: GroupModel) {
    this.professorService.findMembersByTeamId(team.id).subscribe( students => {
      this.dialog.open(ShowTeamMembersComponent, {data: {students, teamName: team.name}})
        .afterClosed()
        .subscribe(result => {

        });
    });
  }

  ngOnDestroy(): void {
    if (this.changeCorsoSub) {
      this.changeCorsoSub.unsubscribe();
    }
  }


}
