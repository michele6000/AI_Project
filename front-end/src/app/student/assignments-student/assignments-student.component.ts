import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-assignments-student',
  templateUrl: './assignments-student.component.html',
  styleUrls: ['./assignments-student.component.css']
})
export class AssignmentsStudentComponent implements OnInit {

  file: any;
  consegne = [
    {
      name: 'Lab 1',
      date: '06/07/2020',
      history: [
        {
          status: 'Rivisto',
          timestamp: '06/07/2020 14:57:12'
        },
        {
          status: 'Consegnato',
          timestamp: '06/07/2020 14:37:12'
        }
      ]
    }
  ];

  constructor() { }

  ngOnInit(): void {
  }

  handleFileSelect($event: any) {
    console.log($event);
    this.file = $event.target.files[0];
  }
}
