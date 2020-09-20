import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {EditHomeworkComponent} from '../../dialog/edit-homework/edit-homework.component';
import {CreateAssignmentComponent} from '../../dialog/create-assignment/create-assignment.component';
import {CourseModel} from '../../models/course.model';
import {ProfessorService} from '../../services/professor.service';
import * as moment from 'moment';

@Component({
  selector: 'app-assignments',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements OnInit {

  consegne: any[];

  columnsElaborati = ['name', 'surname', 'matricola', 'status', 'timestamp'];

  private courseParam: string;
  private corso: CourseModel;

  constructor(private dialog: MatDialog, private router: Router, private activeRoute: ActivatedRoute, private professorService: ProfessorService) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
    this.corso = this.professorService.findCourseByNameUrl(this.courseParam);

    // Ottengo l'elenco di consegne per questo corso
    this.professorService.findAssignmentsByCourse(this.corso.name).subscribe(
      (res) => {
        const consegne = [];
        res.forEach((c) => {
          c.expiryString = moment(c.expiryDate).format('L');
          c.releaseString = moment(c.releaseDate).format('L');

          // @todo Riempire con l'ultimo elaborato per ogni studente
          c.elaborati = [];
          consegne.push(c);
        });
        this.consegne = consegne;
      },
      (error) => {
        console.log(error);
      }
    );

    // Per ogni consegna richiedo l'elenco di elaborati
  }

  edit($event: any) {
    this.dialog.open(EditHomeworkComponent, {data: $event.history})
      .afterClosed()
      .subscribe(result => {

      });
  }

  createAssignment($event) {
    this.dialog.open(CreateAssignmentComponent, {})
      .afterClosed()
      .subscribe(result => {
        console.log(result);
      });
  }
}
