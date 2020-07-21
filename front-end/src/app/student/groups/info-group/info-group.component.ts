import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-info-group',
  templateUrl: './info-group.component.html',
  styleUrls: ['./info-group.component.css']
})
export class InfoGroupComponent implements OnInit {
  columns = ['email', 'name', 'surname', 'matricola'];
  data = [
    {
      email: 's123456',
      name: 'Mario',
      surname: 'Rossi',
      matricola: '123456'
    },
    {
      email: 's123456',
      name: 'Paolo',
      surname: 'Verdi',
      matricola: '123456'
    }
  ];
  groupName = 'Alpha Group';

  constructor() { }

  ngOnInit(): void {
  }

}
