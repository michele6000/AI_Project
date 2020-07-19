import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-create-group',
  templateUrl: './create-group.component.html',
  styleUrls: ['./create-group.component.css']
})
export class CreateGroupComponent implements OnInit {
  studentsColumns = ['Matricola', 'Nome'];
  studentsData = [];
  groupsColumns = ['Nome del gruppo', 'Matricola', 'Nome'];
  groupsData = [];

  constructor() { }

  ngOnInit(): void {
  }

}
