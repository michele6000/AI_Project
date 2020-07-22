import {Component, OnInit} from '@angular/core';
import {EditCourseComponent} from '../../dialog/edit-course/edit-course.component';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {EditHomeworkComponent} from '../../dialog/edit-homework/edit-homework.component';

@Component({
  selector: 'app-assignments',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements OnInit {

  consegne = [
    {
      name: 'Lab 1',
      date: '06/07/2020',
      elaborati: [
        {
          name: 'Mario',
          surname: 'Rossi',
          matricola: '223098',
          status: 'Letto',
          timestamp: ''
        }
      ]
    },
    {
      name: 'Lab 2',
      date: '12/07/2020',
      elaborati: []
    }
  ];
  columnsElaborati = ['name', 'surname', 'matricola', 'status', 'timestamp'];

  constructor(private dialog: MatDialog, private router: Router, private activeRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
  }

  edit($event: any[]) {
    this.dialog.open(EditHomeworkComponent, {data: {}})
      .afterClosed()
      .subscribe(result => {

      });
  }
}
