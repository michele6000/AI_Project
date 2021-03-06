import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {ShowHistoryComponent} from '../../dialog/edit-homework/show-history.component';
import {CreateAssignmentComponent} from '../../dialog/create-assignment/create-assignment.component';
import {CourseModel} from '../../models/course.model';
import {ProfessorService} from '../../services/professor.service';
import * as moment from 'moment';
import {StudentSubmissionModel} from '../../models/student-submission.model';
import {SolutionModel} from '../../models/solution.model';
import {EvaluateSolutionComponent} from '../../dialog/evaluate-solution/evaluate-solution.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ReviewSolutionComponent} from '../../dialog/review-solution/review-solution.component';

const API_URL_PUBLIC = '93.56.104.204:8080/API/';
const API_URL_LOCAL = '/local/API/';


@Component({
  selector: 'app-assignments',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements OnInit, OnDestroy {
  consegne: any[];
  columnsElaborati = ['name', 'surname', 'matricola', 'status'];
  private courseParam: string;
  corso: CourseModel;
  show: boolean;
  loaderDisplayed = false;

  constructor(private dialog: MatDialog, private router: Router, private activeRoute: ActivatedRoute,
              private snackBar: MatSnackBar, private professorService: ProfessorService) {
  }

  loadAssignments() {
    this.loaderDisplayed = true;
    // 1 - Ottengo l'elenco di consegne per il corso in questione
    this.professorService.findAssignmentsByCourse(this.corso.name).subscribe(
      (resSubmissions) => {
        const consegne = [];
        if (resSubmissions.length < 1) {
          this.show = consegne.length !== 0;
          this.loaderDisplayed = false;
        }
        // 2 - Per ogni assignment recupero tutte le solution
        resSubmissions.forEach((submission) => {
          // Formatto correttamente le date
          submission.expiryString = moment(submission.expiryDate).format('DD/MM/YYYY');
          submission.releaseString = moment(submission.releaseDate).format('DD/MM/YYYY');
          // recupero per ogni assignment tutte le solution -> l'elaborazione è spostata lato server
          this.professorService.getAllLatestSolution(submission.id).subscribe(
            allLastSolution => {
              submission.elaborati = allLastSolution;
              consegne.push(submission);
              consegne.sort((a, b) => moment(a.releaseDate).diff(b.releaseDate, 'days'));
              this.consegne = consegne;
              this.show = consegne.length !== 0;
              this.loaderDisplayed = false;
            }
          );
        });
      },
      (error) => {
        this.loaderDisplayed = false;
        this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
          duration: 5000
        });
        setTimeout(location.reload, 5000);
      }
    );
  }

  ngOnInit(): void {
    this.show = false;
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
    this.corso = this.professorService.findCourseByNameUrl(this.courseParam);
    if (this.corso.name.length === 0) {
      return;
    }
    this.loadAssignments();
  }

  ngOnDestroy() {}

  showHistory(studentSub: StudentSubmissionModel) {
    this.dialog.open(ShowHistoryComponent, {data: studentSub, autoFocus: false})
      .afterClosed()
      .subscribe(result => {
      });
  }

  createAssignment($event) {
    this.dialog.open(CreateAssignmentComponent, {restoreFocus: false})
      .afterClosed()
      .subscribe(result => {
        // Dopo aver creato l'assignment aggiorno la tabella (se non è click su cancel nel dialog)
        if (result) {
          this.loadAssignments();
        }
      });
  }

  handleShowSubmission(id: string) {
    window.open('//' + API_URL_PUBLIC + 'courses/submissions/getImage/' + id, '_blank');
  }

  evaluateSolution($event: SolutionModel) {
    // restoreFocus: false
    // per non riportare il focus sul bottone dopo la chiusura della dialog
    this.dialog.open(EvaluateSolutionComponent, {data: $event, restoreFocus: false})
      .afterClosed()
      .subscribe(result => {
        if (result) {
          this.loadAssignments();
        }
      });
  }

  reviewSolution($event: SolutionModel) {
    this.dialog.open(ReviewSolutionComponent, {data: $event})
      .afterClosed()
      .subscribe(result => {
      });
  }
}
