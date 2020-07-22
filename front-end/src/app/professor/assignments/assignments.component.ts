import {Component, OnInit} from '@angular/core';

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

  constructor() {
  }

  ngOnInit(): void {
  }

}
