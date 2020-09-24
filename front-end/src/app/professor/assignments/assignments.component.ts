import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {ShowHistoryComponent} from '../../dialog/edit-homework/show-history.component';
import {CreateAssignmentComponent} from '../../dialog/create-assignment/create-assignment.component';
import {CourseModel} from '../../models/course.model';
import {ProfessorService} from '../../services/professor.service';
import * as moment from 'moment';
import {from} from 'rxjs';
import {concatMap, toArray} from 'rxjs/operators';
import {StudentSubmissionModel} from "../../models/student-submission.model";

const API_URL_PUBLIC = '93.56.104.204:8080/API/';
const API_URL_LOCAL = '/local/API/';


@Component({
  selector: 'app-assignments',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements OnInit {

  consegne: any[];

  columnsElaborati = ['name', 'surname', 'matricola', 'status'];

  private courseParam: string;
  private corso: CourseModel;
  show: boolean;

  constructor(private dialog: MatDialog, private router: Router, private activeRoute: ActivatedRoute, private professorService: ProfessorService) {
  }

  ngOnInit(): void {
    this.show = false;
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
    this.corso = this.professorService.findCourseByNameUrl(this.courseParam);

    // 1 - Recupero l'elenco di studenti del corso
    this.professorService.getEnrolledStudents(this.corso.name).subscribe(
      (resStudents) => {

        // 2 - Ottengo l'elenco di consegne per questo corso
        this.professorService.findAssignmentsByCourse(this.corso.name).subscribe(
          (resSubmissions) => {
            const consegne = [];
            resSubmissions.forEach((submission) => {

              // Formatto correttamente le date
              submission.expiryString = moment(submission.expiryDate).format('L');
              submission.releaseString = moment(submission.releaseDate).format('L');

              // 3 - Per ogni studente recupero getLatestSolution per questa submission
              const resultLatestSolutions = from(resStudents).pipe(
                concatMap(student => {
                  return this.professorService.getLatestSolution(student.id, submission.id);
                }),
                toArray()
              );

              // resultLatestSolutions contiene un unico Observable
              resultLatestSolutions.subscribe((latestSolutions: any[]) => {
                // latestSolutions contiene, per ogni studente, l'ultima soluzione
                const elaborati = [];
                latestSolutions.forEach((latestSol, key) => {
                  latestSol.name = resStudents[key].firstName;
                  latestSol.surname = resStudents[key].name;
                  latestSol.matricola = resStudents[key].id;
                  elaborati.push(latestSol);
                });
                // Aggiungo l'elenco delle latestSolutions per ogni studente alla submission
                submission.elaborati = elaborati;
                consegne.push(submission);

                this.consegne = consegne;
                this.show = consegne.length !== 0;
              });

              // 4   - Per ogni solution devo avere [EvaluateSolution] + Evaluation (colonna a parte, può essere NULL => "")
              //        e [ShowSolution] e [StopRevision] e [ReviewSolution]

              // 4.1 - Togliere da getLatestSolution setStatus(REVISITED), se Solution non esiste ritornare SolutionDTO vuoto
              // 5   - Dialog per ogni studente per visualizzare getHistorySolutions
            });
          },
          (error) => {
            console.log(error);
          }
        );

      }
    );


    // Per ogni consegna richiedo l'elenco di elaborati
  }

  showHistory(studentSub: StudentSubmissionModel) {
    this.dialog.open(ShowHistoryComponent, {data: studentSub})
      .afterClosed()
      .subscribe(result => {

      });
  }

  createAssignment($event) {
    this.dialog.open(CreateAssignmentComponent, {})
      .afterClosed()
      .subscribe(result => {


      });
  }

  handleShowSubmission(id: string) {
    window.open('//' + API_URL_PUBLIC + 'courses/submissions/getImage/' + id, '_blank');
  }
}
