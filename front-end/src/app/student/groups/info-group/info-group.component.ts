import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-info-group',
  templateUrl: './info-group.component.html',
  styleUrls: ['./info-group.component.css']
})
export class InfoGroupComponent implements OnInit {
  columns = ['Matricola', 'Nome'];
  data = [];

  constructor() { }

  ngOnInit(): void {
  }

}
